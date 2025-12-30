package org.example.playground.domain.refreshtoken.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.entity.RefreshToken;
import org.example.playground.domain.refreshtoken.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 토큰 저장
    public RefreshToken createRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    // 토큰 조회
    @Transactional(readOnly = true)
    public RefreshToken getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 존재하지 않습니다."));
    }

    // 토큰 삭제
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }
}
