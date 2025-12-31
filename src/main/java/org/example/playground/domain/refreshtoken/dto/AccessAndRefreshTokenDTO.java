package org.example.playground.domain.refreshtoken.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.playground.global.security.jwt.dto.TokenDTO;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class AccessAndRefreshTokenDTO {
    private String accessToken;
    private TokenDTO refreshToken;

    public static AccessAndRefreshTokenDTO from(String accessToken, TokenDTO refreshToken) {
        return AccessAndRefreshTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
