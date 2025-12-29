package org.example.playground.domain.refreshtoken.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.entity.RefreshToken;
import org.example.playground.domain.refreshtoken.service.RefreshTokenService;
import org.example.playground.global.security.jwt.JwtTokenProvider;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 리프레시토큰 찾기
        String refreshToken = getRefreshToken(request);
        // 토큰 x -> 에러 반환
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh Token이 존재하지 않습니다.");
        }

        // 토큰 검증, 파싱
        // 나중에 전역처리기에 예외 작성해야함.(토큰만료,유효하지않은토큰 등)
        Claims claims = jwtTokenProvider.parseRefreshToken(refreshToken);

        // 쿠키에서 꺼낸 토큰으로 db에 저장된 토큰과 일치하는지 검증
        // 일치하지 않는다면 -> 에러반환
        RefreshToken dbToken = refreshTokenService.getRefreshToken(refreshToken);
        if (!dbToken.getToken().equals(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다.");
        }

        // 사용자 정보에서 id값,권한을 꺼내서 토큰 발급
        Long sub = Long.parseLong(claims.getSubject());
        // Claims에 권한이 저장될때 List<?>로 저장되기때문에 변환과정이 필요함.
        List<?> rowRoles = claims.get("roles", List.class);
        List<String> roles = rowRoles.stream()
                .map(Object::toString)
                .toList();
        TokenDTO tokenDTO = jwtTokenProvider.createAccessToken(sub, roles);
        String accessToken = tokenDTO.getToken();

        return ResponseEntity.ok(Map.of("accessToken",accessToken));
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
