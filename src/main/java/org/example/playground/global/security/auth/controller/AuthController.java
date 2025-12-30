package org.example.playground.global.security.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.dto.AccessAndRefreshTokenDTO;
import org.example.playground.global.security.auth.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

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
}
