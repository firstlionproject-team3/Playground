package org.example.playground.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.UserRegisterDTO;
import org.example.playground.domain.user.dto.UserRegisterSuccessDTO;
import org.example.playground.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    //회원 가입
    @PostMapping("/sign-up")
    public ResponseEntity<UserRegisterSuccessDTO> createUser(
            @Valid @RequestBody UserRegisterDTO userDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        //TODO 인증/인가 관련 로직은 시큐리티에서 처리하는 것이 나을 것 같은..?
//        if(userDetails != null){
//            throw new AccessDeniedException("이미 로그인된 사용자는 회원가입을 할 수 없습니다.");
//        }

        UserRegisterSuccessDTO user = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    //마이페이지에서 삭제
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        //TODO 인증/인가 관련 로직은 시큐리티에서 처리하는 것이 나을 것 같은..?
//        if(userDetails == null){
//            throw new AccessDeniedException("회원 탈퇴는 로그인을 한 후에 가능합니다");
//        }

        userService.deleteUser(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    //TODO GET /me (마이페이지)

    //TODO POST /me (마이페이지 정보수정)

    //TODO GET /me/questions?page=0&size=20 (마이페이지 - 질문 목록)

    //TODO GET /me/answers?page=0&size=20 (마이페이지 에서 내가 쓴 댓글 클릭시 - 댓글 목록)
}
