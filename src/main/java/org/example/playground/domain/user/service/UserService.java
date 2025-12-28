package org.example.playground.domain.user.service;

import org.example.playground.domain.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    //회원 가입
    public UserRegisterSuccessDTO createUser(UserRegisterDTO userDTO);

    public OAuth2ResponseForJWT handleOAuth2Login(OAuth2UserInfo oAuthUserInfo);

    //회원 탈퇴
    public void deleteUser(Long id, UserDetails userDetails);

    //TODO 회원 마이페이지용 유저 정보 조회 메서드, 질문 답변 도메인 담당자에게 유저 본인 질문, 댓글 목록 조회 메서드 작성 요청하기
    public UserDetailDTO getUser(Long id, UserDetails userDetails);

    //TODO 회원정보 수정 메서드
    public UserDetailDTO updateUser(Long id, UserDetails userDetails, UserDetailDTO userDetailDTO);

    //모든 회원 목록
    public Page<UserSummaryDTO> getUsers(UserDetails userDetails);

    //회원의 모든 댓글 조회

    //회원의 모든 게시글 조회
}
