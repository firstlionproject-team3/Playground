package org.example.playground.global.security.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.dto.AccessAndRefreshTokenDTO;
import org.example.playground.global.security.login.service.LoginService;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.example.playground.global.security.user.dto.LoginRequestDTO;
import org.example.playground.global.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final LoginService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO,
                                   HttpServletResponse response) {

        AccessAndRefreshTokenDTO accessAndRefreshTokenDTO = authService.login(loginRequestDTO);
        String accessToken = accessAndRefreshTokenDTO.getAccessToken();

        // refreshToken 쿠키에 저장
        TokenDTO refreshToken = accessAndRefreshTokenDTO.getRefreshToken();
        CookieUtil.addRefreshToken(response,refreshToken);

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);
        return ResponseEntity.noContent().build();
    }
}
