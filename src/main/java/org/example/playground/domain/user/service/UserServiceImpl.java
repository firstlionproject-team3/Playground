package org.example.playground.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.domain.user.dto.*;
import org.example.playground.domain.user.entity.Role;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.DuplicateUserException;
import org.example.playground.domain.user.exception.OAuth2SignedupException;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.RoleRepository;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.security.user.CustomUserDetails;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static org.example.playground.domain.user.dto.UserRegisterResponseDTO.userRegisterResponseDTOfromEntity;
import static org.example.playground.domain.user.entity.User.userFromDTO;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserRegisterResponseDTO createUser(UserRegisterRequestDTO userDTO) {
        if (userRepository.existsByLoginId(userDTO.getLoginId())) {
            throw new DuplicateUserException("이미 존재하는 로그인 ID입니다");
        }

        // 비밀번호 인코딩 과정
        // DB에 넣을 인스턴스 가공
        String encodingPW = passwordEncoder.encode(userDTO.getPassword());
        User user = userFromDTO(userDTO, encodingPW);

        addUserRole(user);

        return userRegisterResponseDTOfromEntity(userRepository.save(user));
    }

    //TODO 재현님쪽으로 가는 메서드
    @Override
    public SecurityResponseForJWT handleLogin(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException("유저가 존재하지 않습니다"));

        return SecurityResponseForJWT.securityResponseFromUser(user);
    }

    @Override
    @Transactional
    //OAuth2 인증을 성공한 유저가 회원이 아니라면 회원테이블에 추가하는 로직
    //반환타입은 토큰 발급에 필요한 두개의 필드를 가진 별도의 타입
    public SecurityResponseForJWT handleOAuth2Login(OAuth2UserInfo info) {
        User user = findOAuth2User(info)
                .orElseGet(() -> raceHandler(info));   // 가입

        // 여기서 로그인 처리 의미는 사용자 식별 완료
        return SecurityResponseForJWT.securityResponseFromUser(user);
    }

    //Race Condition check. 동일 provider/providerId로 동시 요청 시 유니크 충돌 복구
    private User raceHandler(OAuth2UserInfo info) {
        try {
            return registerOAuth2User(info);
        } catch (DataIntegrityViolationException e) {
            //그 사이에 이미 회원등록이 된 경우
            return findOAuth2User(info).orElseThrow(()
                    -> {
                //race condition 상황이 아닐때(ex> 제약위반)
                log.warn("OAuth2 회원 추가 실패 그러나 DB에서 회원 발견되지 않음. provider={}, providerId={}", info.getProvider(), info.getProviderId(), e);
                throw new OAuth2SignedupException("소셜 로그인 처리 중 오류가 발생했습니다");
            });
        }
    }

    //로그인한 유저인지 찾는 메서드
    private Optional<User> findOAuth2User(OAuth2UserInfo info) {
        return userRepository.findUserByProviderAndProviderId(info.getProvider(), info.getProviderId());
    }

    //회원테이블에 없다면 새로 등록하는 회원가입 메서드
    private User registerOAuth2User(OAuth2UserInfo info) {
        User user = User.userFromOAuthUser(info, passwordEncoder);
        addUserRole(user);
        return userRepository.save(user);
    }

    //기본적으로 USER 권한 부여. 만약 roles 테이블에 USER 이 없을 시 새로 만들어서 USER 부여. (첫 회원)
    private void addUserRole(User user) {
        user.addRole(roleRepository.findByName("ROLE_USER").orElseGet(()
                -> roleRepository.save(new Role("ROLE_USER"))));
    }

    //회원 마이페이지용 유저 정보 조회 메서드
    @Override
    @Transactional(readOnly = true)
    public UserMyPageResponseDTO getUser(CustomUserDetails currentUser) {
        User findUser = findUserFromDB(currentUser.getId());

        return UserMyPageResponseDTO.userMyPageDTOFromEntity(findUser);
    }

    // 회원정보 수정 메서드
    @Override
    @Transactional
    public UserMyPageResponseDTO updateUser(CustomUserDetails currentUser, UserUpdateRequestDTO userUpdateRequestDTO) {
        User findUser = findUserFromDB(currentUser.getId());

        if (!findUser.getName().equals(userUpdateRequestDTO.getName())) {
            findUser.changeName(userUpdateRequestDTO.getName());
        }

        if (!Objects.equals(findUser.getEmail(), userUpdateRequestDTO.getEmail())){
            findUser.changeEmail(userUpdateRequestDTO.getEmail());
        }

        return UserMyPageResponseDTO.userMyPageDTOFromEntity(findUser);
    }

    @Override
    @Transactional
    public void deleteUser(CustomUserDetails currentUser) {
        User findUser = findUserFromDB(currentUser.getId());
        userRepository.delete(findUser);
    }

    // 회원정보 검색하는 메서드
    private User findUserFromDB(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("존재하지 않는 회원입니다."));
    }
}

