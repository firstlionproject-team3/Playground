package org.example.playground.domain.user.service;

import org.example.playground.domain.user.dto.UserDTO;
import org.example.playground.domain.user.dto.UserRegisterDTO;
import org.example.playground.domain.user.exception.DuplicateUserException;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    //회원 가입
    public UserRegisterDTO createUser(UserDTO userDTO) throws DuplicateUserException;

    //회원 정보 수정

    //회원 탈퇴
    public void deleteUser(Long id, UserDetails userDetails) throws UserNotFoundException;

    //회원 정보 조회
//    public UserDTO getUser(Integer id, UserDetails userDetails);

    //모든 회원 목록

    //회원의 모든 댓글 조회

    //회원의 모든 게시글 조회
}
