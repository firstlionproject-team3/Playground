package org.example.playground.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.AnotherUserException;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService{
    private final UserRepository userRepository;

    //TODO 관리자만 유저 목록 조회
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')") // 내부적으로 "ROLE_ADMIN"을 기대함
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')") // 내부적으로 "ROLE_ADMIN"을 기대함
    public void deleteUser(Long id) {
        User findUser = findUserFromDB(id);
        userRepository.delete(findUser);
    }

    // 회원정보 검색하는 메서드
    private User findUserFromDB(Long id){
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("존재하지 않는 회원입니다."));
    }
}
