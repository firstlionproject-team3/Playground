package org.example.playground.domain.user.service;

import org.example.playground.domain.user.dto.OAuth2ResponseForJWT;
import org.example.playground.domain.user.dto.OAuth2UserInfo;
import org.example.playground.domain.user.dto.UserRegisterDTO;
import org.example.playground.domain.user.dto.UserRegisterSuccessDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    //회원 가입
    public UserRegisterSuccessDTO createUser(UserRegisterDTO userDTO);

    public OAuth2ResponseForJWT handleOAuth2Login(OAuth2UserInfo oAuthUserInfo);
    //회원 정보 수정


    //회원 탈퇴
    public void deleteUser(Long id, UserDetails userDetails);

    //회원 정보 조회
//    public UserDTO getUser(Integer id, UserDetails userDetails);

    //모든 회원 목록

    //회원의 모든 댓글 조회

    //회원의 모든 게시글 조회
}
