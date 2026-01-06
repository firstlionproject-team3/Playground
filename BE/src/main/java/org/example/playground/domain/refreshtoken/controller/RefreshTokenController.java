package org.example.playground.domain.refreshtoken.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.dto.AccessAndRefreshTokenDTO;
import org.example.playground.domain.refreshtoken.service.RefreshTokenService;
import org.example.playground.global.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 리프레시토큰 찾기
        String refreshToken = CookieUtil.getToken(request,"refreshToken");
        // 토큰 x -> 에러 반환
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh Token이 존재하지 않습니다.");
        }

        // accessToken 재발급, refreshToken 로테이션 적용
        AccessAndRefreshTokenDTO accessAndRefreshTokenDTO = refreshTokenService.reissueToken(refreshToken);

        // 새로 만든 refreshToken 쿠키에 추가
        CookieUtil.addRefreshToken(response, accessAndRefreshTokenDTO.getRefreshToken());

        return ResponseEntity.ok(Map.of("accessToken", accessAndRefreshTokenDTO.getAccessToken()));
    }
}
