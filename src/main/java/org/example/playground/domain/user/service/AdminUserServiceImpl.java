package org.example.playground.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.repository.RefreshTokenRepository;
import org.example.playground.domain.user.dto.UserMyPageResponseDTO;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserException;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.playground.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService{
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 관리자만 유저 목록 조회
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')") // 내부적으로 "ROLE_ADMIN"을 기대함
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public UserMyPageResponseDTO getUser(Long id) {
        User findUser = findUserOrThrow(id);

        return UserMyPageResponseDTO.userMyPageDTOFromEntity(findUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')") // 내부적으로 "ROLE_ADMIN"을 기대함
    public void deleteUser(Long id) {
        User findUser = findUserOrThrow(id);
        refreshTokenRepository.deleteByUserId(id);

        findUser.softDeleteAndAnonymize();
        userRepository.save(findUser);
    }

    // 회원정보 검색하는 메서드
    @Override
    @Transactional(readOnly = true)
    public User findUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserException(USER_NOT_FOUND));
    }
}
