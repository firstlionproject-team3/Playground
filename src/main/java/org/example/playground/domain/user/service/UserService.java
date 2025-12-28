package org.example.playground.domain.user.service;

import org.example.playground.domain.user.dto.OAuth2ResponseForJWT;
import org.example.playground.domain.user.dto.OAuthUserInfo;
import org.example.playground.domain.user.dto.UserDTO;
import org.example.playground.domain.user.dto.UserRegisterDTO;
import org.example.playground.domain.user.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    //회원 가입
    public UserRegisterDTO createUser(UserDTO userDTO);

    public OAuth2ResponseForJWT handleOAuthLogin(OAuthUserInfo oAuthUserInfo);
    //회원 정보 수정


    //회원 탈퇴
    public void deleteUser(Long id, UserDetails userDetails);

    //회원 정보 조회
//    public UserDTO getUser(Integer id, UserDetails userDetails);

    //모든 회원 목록

    //회원의 모든 댓글 조회

    //회원의 모든 게시글 조회
}
