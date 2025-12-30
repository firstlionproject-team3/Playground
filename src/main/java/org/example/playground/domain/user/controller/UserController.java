package org.example.playground.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.UserMyPageResponseDTO;
import org.example.playground.domain.user.dto.UserRegisterRequestDTO;
import org.example.playground.domain.user.dto.UserRegisterResponseDTO;
import org.example.playground.domain.user.dto.UserUpdateRequestDTO;
import org.example.playground.domain.user.service.UserService;
import org.example.playground.global.security.user.CustomUserDetails;
import org.hibernate.query.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    //회원 가입
    @PostMapping
    public ResponseEntity<UserRegisterResponseDTO> createUser(
            @Valid @RequestBody UserRegisterRequestDTO userDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        //TODO 인증/인가 관련 로직은 시큐리티에서 처리하는 것이 나을 것 같은..?
//        if(userDetails != null){
//            throw new AccessDeniedException("이미 로그인된 사용자는 회원가입을 할 수 없습니다.");
//        }

        UserRegisterResponseDTO user = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // GET /me (마이페이지)
    @GetMapping("/me")
    public ResponseEntity<UserMyPageResponseDTO> myPage(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(userService.getUser(userDetails.getId()));
    }

    // PATCH /me (마이페이지 정보수정)
    @PatchMapping("/me")
    public ResponseEntity<UserMyPageResponseDTO> myPageUpdate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO
            ){
        return ResponseEntity.ok(userService.updateUser(userDetails, userUpdateRequestDTO));
    }

    //TODO GET /me/questions?page=0&size=20 (마이페이지 - 질문 목록)
//    @GetMapping("/questions")


    //TODO GET /me/answers?page=0&size=20 (마이페이지 에서 내가 쓴 댓글 클릭시 - 댓글 목록)
//    @GetMapping("/answers")

    //마이페이지에서 삭제
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        //TODO 인증/인가 관련 로직은 시큐리티에서 처리하는 것이 나을 것 같은..?
//        if(userDetails == null){
//            throw new AccessDeniedException("회원 탈퇴는 로그인을 한 후에 가능합니다");
//        }

        userService.deleteUser(userDetails);
        return ResponseEntity.noContent().build();
    }
}
