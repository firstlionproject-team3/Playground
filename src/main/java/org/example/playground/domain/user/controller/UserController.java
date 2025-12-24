package org.example.playground.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.UserDTO;
import org.example.playground.domain.user.dto.UserRegisterDTO;
import org.example.playground.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class UserController {
    private final UserService userService;

    //회원 가입
    @PostMapping("/sign-up")
    public ResponseEntity<UserRegisterDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserRegisterDTO user = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
