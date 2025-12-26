package org.example.playground.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.UserDTO;
import org.example.playground.domain.user.dto.UserRegisterDTO;
import org.example.playground.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class UserController {
    private final UserService userService;

    //회원 가입
    @PostMapping("/sign-up")
    public ResponseEntity<UserRegisterDTO> createUser(
            @Valid @RequestBody UserDTO userDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if(userDetails != null){
            throw new AccessDeniedException("이미 로그인된 사용자는 회원가입을 할 수 없습니다.");
        }

        UserRegisterDTO user = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        if(userDetails == null){
            throw new AccessDeniedException("회원 탈퇴는 로그인을 한 후에 가능합니다");
        }

        userService.deleteUser(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
