package org.example.playground.domain.user.service;

import org.example.playground.domain.user.dto.*;
import org.example.playground.global.security.user.CustomUserDetails;

public interface UserService {
    //회원 가입
    UserRegisterResponseDTO createUser(UserRegisterRequestDTO userDTO);

//    //security 일반 로그인 용 메서드
//    SecurityResponseForJWT loadForTokenIssue(String loginId);

    SecurityResponseForJWT handleOAuth2Login(OAuth2UserInfo oAuthUserInfo);

    //TODO 회원 마이페이지용 유저 정보 조회 메서드
    UserMyPageResponseDTO getUser(Long id);

    //TODO 회원정보 수정 메서드
    UserMyPageResponseDTO updateUser(Long id, UserUpdateRequestDTO userUpdateRequestDTO);

    //회원 탈퇴
    void deleteUser(Long id);

    //회원의 모든 댓글 조회

    //회원의 모든 게시글 조회
}
