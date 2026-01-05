package org.example.playground.global.security.jwt.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class TokenDTO {

    private String token;

    private Date expiration;

    public static TokenDTO from(String token, Date expiration) {
        return TokenDTO.builder()
                .token(token)
                .expiration(expiration)
                .build();
    }
}
