package org.example.playground.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.entity.RefreshToken;
import org.example.playground.domain.refreshtoken.service.RefreshTokenService;
import org.example.playground.global.security.jwt.JwtTokenProvider;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.example.playground.global.security.user.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

// 일반 로그인 성공했을 때 호출
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        TokenDTO accessToken = jwtTokenProvider.createAccessToken(user.getId(), roles);
        TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        Date expiration = refreshToken.getExpiration();

        // RefreshToken DB에 저장
        RefreshToken refreshTokenEntity =
                RefreshToken.from(
                        user.getId(),
                        refreshToken.getToken(),
                        expiration);
        refreshTokenService.createRefreshToken(refreshTokenEntity);

        // refreshToken -> HttpOnly 쿠키에 담아준다.(자바스크립트에서 접근불가)
        // 쿠키는 Date타입을 받지않고 초단위로 만료시간을 계산한다.
        long expSec = expiration.getTime() - System.currentTimeMillis();
        // 만료된 경우 0
        int maxAge = expSec > 0 ? (int) (expSec / 1000) : 0;

        // 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken.getToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAge);
        response.addCookie(refreshTokenCookie);

        // accessToken 응답
        Map<String, Object> body = new HashMap<>();
        body.put("accessToken", accessToken.getToken());
        String responseJson = objectMapper.writeValueAsString(body);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(responseJson);

    }
}
