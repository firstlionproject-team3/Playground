package org.example.playground.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.domain.user.dto.*;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.DuplicateUserException;
import org.example.playground.domain.user.exception.OAuth2SignedupException;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.RoleRepository;
import org.example.playground.domain.user.repository.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.example.playground.domain.user.dto.UserRegisterResponseDTO.userRegisterResponseDTOfromEntity;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int NAME_RETRY = 5;
    private static final String UK_NICKNAME = "uk_user_nickname";
    private static final String UK_LOGIN_ID = "uk_user_login_id";
    private static final String UK_PROVIDER = "uk_user_provider_providerId";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserFactory userFactory;

    @Override
    @Transactional
    public UserRegisterResponseDTO createUser(UserRegisterRequestDTO userDTO) {
        for (int attempt = 1; attempt <= NAME_RETRY; attempt++) {
            User user = userFactory.createLocal(userDTO, userFactory.newAutoNickname());
            try {
                User saved = saveUserWithDefaultRole(user);
                return userRegisterResponseDTOfromEntity(saved);
            } catch (DataIntegrityViolationException e) {
                // loginId는 사용자 입력 → 즉시 실패
                if (isLoginIdDuplicate(e)) {
                    throw new DuplicateUserException("이미 존재하는 로그인 ID입니다");
                }

                // nickname은 자동 생성 → 재시도
                if (isNicknameDuplicate(e)) {
                    log.debug("닉네임 충돌로 재시도합니다. attempt={}/{}", attempt, NAME_RETRY);
                    continue;
                }

                // 기타 무결성 위반/DB 예외는 그대로 올림(운영 정책에 따라 도메인 예외로 감싸도 됨)
                throw e;
            }
        }

        throw new IllegalStateException("이름 생성 충돌이 반복되어 회원가입에 실패했습니다. 잠시 후 다시 시도하세요.");
    }

    @Override
    @Transactional
    //OAuth2 인증을 성공한 유저가 회원이 아니라면 회원테이블에 추가하는 로직
    //반환타입은 토큰 발급에 필요한 두개의 필드를 가진 별도의 타입
    public SecurityResponseForJWT handleOAuth2Login(OAuth2UserInfo info) {
        User user = findOrCreateOAuthUser(info);   // 가입

        // 여기서 로그인 처리 의미는 사용자 식별 완료
        return SecurityResponseForJWT.securityResponseFromUser(user);
    }

    //회원 마이페이지용 유저 정보 조회 메서드
    @Override
    @Transactional(readOnly = true)
    public UserMyPageResponseDTO getUser(Long userId) {
        User findUser = findUserOrThrow(userId);

        return UserMyPageResponseDTO.userMyPageDTOFromEntity(findUser);
    }

    // 회원정보 수정 메서드
    @Override
    @Transactional
    public UserMyPageResponseDTO updateUser(Long userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        User findUser = findUserOrThrow(userId);

        if (!Objects.equals(findUser.getNickname(), userUpdateRequestDTO.getNickname())) {
            findUser.changeNickname(userUpdateRequestDTO.getNickname());
            try {
                userRepository.flush();
            } catch (DataIntegrityViolationException e) {
                if (isNicknameDuplicate(e)) {
                    throw new DuplicateUserException("이미 사용 중인 이름입니다");
                }
                throw e;
            }
        }

        if (!Objects.equals(findUser.getEmail(), userUpdateRequestDTO.getEmail())) {
            findUser.changeEmail(userUpdateRequestDTO.getEmail());
        }

        return UserMyPageResponseDTO.userMyPageDTOFromEntity(findUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User findUser = findUserOrThrow(userId);
//todo 주석 풀기
//        refreshTokenRepository.deleteByUserId(userId);

        findUser.softDeleteAndAnonymize();
        userRepository.save(findUser);
    }

    //가능하면 constraintName을 제공하는 Hibernate 예외를 우선 확인하고, 없으면 메시지 fallback을 사용한다
    private boolean hasConstraint(Throwable e, String constraintName) {
        Throwable t = e;

        while (t != null) {

            // 1. Hibernate ConstraintViolationException 우선 시도
            // ConstraintViolationException → “DB의 어떤 규칙이 깨졌는지”를 아는 예외 ( null일 수 있음 )
            if (t instanceof ConstraintViolationException cve) {
                String violated = cve.getConstraintName();
                if (constraintName.equals(violated)) {
                    return true;
                }
            }

            // 2️. fallback: 메시지 기반 파싱
            // DataIntegrityViolationException → “데이터 무결성에 문제가 생겼다”라고 Spring이 포괄적으로 감싸서 던지는 예외
            String msg = t.getMessage();
            if (msg != null && msg.contains(constraintName)) {
                return true;
            }

            t = t.getCause();
        }

        return false;
    }

    private boolean isNicknameDuplicate(Throwable e) {
        return hasConstraint(e, UK_NICKNAME);
    }

    private boolean isLoginIdDuplicate(Throwable e) {
        return hasConstraint(e, UK_LOGIN_ID);
    }

    private boolean isProviderDuplicate(Throwable e) {
        return hasConstraint(e, UK_PROVIDER);
    }

    // 회원정보 검색하는 메서드
    @Transactional(readOnly = true)
    public User findUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("존재하지 않는 회원입니다."));
    }

    //로그인한 유저인지 찾는 메서드
    private User findOrCreateOAuthUser(OAuth2UserInfo info) {
        return userRepository.findUserByProviderAndProviderId(info.getProvider(), info.getProviderId())
                .map(found -> {
                    if (found.isDeleted()) {
                        throw new OAuth2SignedupException("탈퇴한 계정입니다.");
                    }
                    return found;
                })
                .orElseGet(() -> createOAuthUserWithRetry(info));
    }

    //Race Condition check. 동일 provider/providerId로 동시 요청 시 유니크 충돌 복구
    private User createOAuthUserWithRetry(OAuth2UserInfo info) {
        for (int attempt = 1; attempt <= NAME_RETRY; attempt++) {
            User user = userFactory.createOAuth(info, userFactory.newAutoNickname());
            try {
                return saveUserWithDefaultRole(user);
            } catch (DataIntegrityViolationException e) {
                // provider/providerId 유니크 충돌: 다른 요청이 먼저 가입했을 확률 ↑ → 재조회로 회수
                if (isProviderDuplicate(e)) {
                    return userRepository.findUserByProviderAndProviderId(info.getProvider(), info.getProviderId())
                            .orElseThrow(() -> e);
                }

                // nickname 유니크 충돌: 닉네임만 바꿔 재시도
                if (isNicknameDuplicate(e)) {
                    log.debug("OAuth 닉네임 충돌로 재시도합니다. attempt={}/{}", attempt, NAME_RETRY);
                    continue;
                }

                // loginId(= provider:providerId) 유니크 충돌이 난다면 설계상 provider 충돌과 동치에 가까움.
                // 그래도 혹시 모르니 방어적으로 provider 재조회 시도 후 실패면 예외 전파
                if (isLoginIdDuplicate(e)) {
                    return userRepository.findUserByProviderAndProviderId(info.getProvider(), info.getProviderId())
                            .orElseThrow(() -> e);
                }

                throw e;
            }
        }
        throw new OAuth2SignedupException("소셜 로그인 처리 중 오류가 발생했습니다");
    }

    // 기본 ROLE_USER를 부여한 뒤 저장(즉시 flush하여 유니크 위반을 현재 스코프에서 감지)
    private User saveUserWithDefaultRole(User user) {
        addUserRole(user);
        return userRepository.saveAndFlush(user);
    }

    //기본적으로 USER 권한 부여
    private void addUserRole(User user) {
        user.addRole(roleRepository.findByName("ROLE_USER").orElseThrow(()
                -> new IllegalStateException("ROLE_USER가 존재하지 않습니다. DB 초기화 상태를 확인하세요.")));
    }
}

