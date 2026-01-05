package org.example.playground.domain.user.service;

import org.example.playground.domain.user.dto.UserMyPageResponseDTO;
import org.example.playground.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService{
    //모든 회원 목록
    Page<User> getAllUsersByAdmin(Pageable pageable);
    UserMyPageResponseDTO getUserByAdmin(Long id);
    void deleteUserByAdmin(Long id);
    User findUserOrThrowByAdmin(Long id);
}
