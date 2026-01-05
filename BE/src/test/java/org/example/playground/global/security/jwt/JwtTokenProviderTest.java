package org.example.playground.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(classes = JwtTokenProvider.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    @Order(1)
    void accessToken_생성및파싱() {
        Long userId = 1L;
        List<String> roles = List.of("ROLE_USER");

        TokenDTO accessToken = jwtTokenProvider.createAccessToken(userId, roles);
        Claims claims = jwtTokenProvider.parseAccessToken(accessToken.getToken());

        System.out.println("accessToken = " + accessToken.getToken());
        List<String> roles1 = claims.get("roles", List.class);

        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(roles1).contains("ROLE_USER");
    }

    @Test
    @Order(2)
    void refreshToken_생성및파싱(){
        Long userId = 1L;

        TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(userId);
        Claims claims = jwtTokenProvider.parseRefreshToken(refreshToken.getToken());

        System.out.println("refreshToken = " + refreshToken.getToken());

        assertThat(claims.getSubject()).isEqualTo("1");
    }

    @Test
    @Order(3)
    void accessToken_만료테스트() throws InterruptedException {
        Long userId = 1L;
        List<String> roles = List.of("ROLE_USER");

        TokenDTO accessToken = jwtTokenProvider.createAccessToken(userId, roles);

        Thread.sleep(1200);

        assertThatThrownBy(() -> jwtTokenProvider.parseAccessToken(accessToken.getToken()))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @Order(4)
    void refreshToken_만료테스트() throws InterruptedException {
        Long userId = 1L;

        TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(userId);

        Thread.sleep(2200);

        assertThatThrownBy(() -> jwtTokenProvider.parseRefreshToken(refreshToken.getToken()))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @Order(5)
    void accessToken_변조테스트() {
        Long userId = 1L;
        List<String> roles = List.of("ROLE_USER");

        TokenDTO accessToken = jwtTokenProvider.createAccessToken(userId, roles);

        String token = accessToken.getToken();
        String tamperedToken = token.substring(token.length() - 2) + "ab";

        assertThatThrownBy(() -> jwtTokenProvider.parseAccessToken(tamperedToken))
                .isInstanceOf(MalformedJwtException.class);

    }

    @Test
    @Order(6)
    void refreshToken_변조테스트() {
        Long userId = 1L;

        TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(userId);

        String token = refreshToken.getToken();
        String tamperedToken = token.substring(token.length() - 2) + "ab";

        assertThatThrownBy(() -> jwtTokenProvider.parseRefreshToken(tamperedToken))
                .isInstanceOf(MalformedJwtException.class);

    }

}