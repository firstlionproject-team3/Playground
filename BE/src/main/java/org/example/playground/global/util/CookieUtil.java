package org.example.playground.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    public static void deleteRefreshToken(HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    public static String getToken(HttpServletRequest request, String type) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(type)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
