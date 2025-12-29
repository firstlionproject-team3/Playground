package org.example.playground.global.util;

import jakarta.servlet.http.HttpServletResponse;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Date;

public final class CookieUtil {
    public static void addRefreshToken(HttpServletResponse response, TokenDTO refreshToken) {
        Date expiration = refreshToken.getExpiration();
        long expTime = expiration.getTime() - System.currentTimeMillis();
        int maxAge = Math.toIntExact(expTime / 1000);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
}
