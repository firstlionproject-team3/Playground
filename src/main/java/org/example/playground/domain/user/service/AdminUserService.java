package org.example.playground.domain.user.service;

import org.example.playground.domain.user.dto.UserMyPageResponseDTO;
import org.example.playground.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService{
    //모든 회원 목록
    Page<User> getAllUsers(Pageable pageable);
    UserMyPageResponseDTO getUser(Long id);
    void deleteUser(Long id);
    User findUserOrThrow(Long id);
}
