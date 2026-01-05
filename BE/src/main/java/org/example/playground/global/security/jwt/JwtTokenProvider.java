package org.example.playground.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;

    public JwtTokenProvider(@Value("${jwt.access-secret}") String accessSecret,
                            @Value("${jwt.refresh-secret}") String refreshSecret,
                            @Value("${jwt.access-expiration-ms}") String accessTokenExpiration,
                            @Value("${jwt.refresh-expiration-ms}") String refreshTokenExpiration)
    {
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenExpiration = Long.parseLong(accessTokenExpiration);
        this.refreshTokenExpiration = Long.parseLong(refreshTokenExpiration);
    }

    /* 토큰 발급 파트 */

    // jwt signature에 사용될 비밀키 생성 (hmac 기반)
    private SecretKey getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

    // 토큰 생성
    // 절대 변하지 않는 id 값을 subject로 사용, 민감한 정보(password)등은 넣으면 안된다.
    // accessToken은 id, roles / refreshToken은 id만 넣어서 최소화한다.
    // 다른 정보들을 accessToken에 넣어 사용하면 db접근을 최소화할 수 있기도하다.

    // access 토큰 발급
    public TokenDTO createAccessToken(Long id, List<String> roles) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);
        String token = Jwts.builder()
                .subject(String.valueOf(id))
                .claim("roles",roles)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey(accessSecret))
                .compact();
        return TokenDTO.from(token, expiration);
    }
    // refresh 토큰 발급
    public TokenDTO createRefreshToken(Long id) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);
        String token = Jwts.builder()
                .subject(String.valueOf(id))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey(refreshSecret))
                .compact();
        return TokenDTO.from(token, expiration);
    }

    /* 토큰 검증 파트 */

    // 토큰을 파싱하는 메서드
    private Claims parseToken(String token, byte[] secret) {
        return Jwts.parser()
                .verifyWith(getSigningKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // AccessToken 파싱
    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    // RefreshToken 파싱
    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

}
