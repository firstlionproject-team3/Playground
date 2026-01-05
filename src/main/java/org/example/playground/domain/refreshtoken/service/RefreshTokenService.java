package org.example.playground.domain.refreshtoken.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.dto.AccessAndRefreshTokenDTO;
import org.example.playground.domain.refreshtoken.entity.RefreshToken;
import org.example.playground.domain.refreshtoken.exception.RefreshTokenErrorCode;
import org.example.playground.domain.refreshtoken.exception.RefreshTokenException;
import org.example.playground.domain.refreshtoken.repository.RefreshTokenRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserErrorCode;
import org.example.playground.domain.user.exception.UserException;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.security.jwt.JwtTokenProvider;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 토큰 저장
    public RefreshToken createRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    // 토큰 조회
    @Transactional(readOnly = true)
    public RefreshToken getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    // 토큰 삭제
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    // accessToken 재발급, refreshToken 로테이션
    public AccessAndRefreshTokenDTO reissueToken(String refreshToken) {

        // 토큰 검증, 파싱
        // 나중에 전역처리기에 예외 작성해야함.(토큰만료,유효하지않은토큰 등)
        Claims claims = jwtTokenProvider.parseRefreshToken(refreshToken);
        // 사용자 정보에서 id값,권한을 꺼내서 토큰 발급
        Long sub = Long.parseLong(claims.getSubject());

        // RefreshToken의 Claims에는 권한이 없기때문에 id로 db에 접근해 권한을 가져와야한다.
        User user = userRepository.findById(sub)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        List<String> roles = user.getRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .toList();

        // 쿠키에서 꺼낸 토큰으로 db에 저장된 토큰과 일치하는지 검증
        // 일치하지 않는다면 -> 에러반환
        RefreshToken dbToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_NOT_FOUND));
        if (!dbToken.getToken().equals(refreshToken)) {
            throw new RefreshTokenException(RefreshTokenErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 로테이션 - 한 번 사용된 RefreshToken은 즉시 폐기하고 새로 발급하여 DB에 교체해주는 방식.
        refreshTokenRepository.deleteByToken(refreshToken);
        TokenDTO newRefreshTokenDTO = jwtTokenProvider.createRefreshToken(sub);

        RefreshToken newRefreshToken = RefreshToken.from(sub, newRefreshTokenDTO.getToken(), newRefreshTokenDTO.getExpiration());
        refreshTokenRepository.save(newRefreshToken);

        // accessToken 발급
        TokenDTO tokenDTO = jwtTokenProvider.createAccessToken(sub, roles);
        String accessToken = tokenDTO.getToken();

        return AccessAndRefreshTokenDTO.from(accessToken, newRefreshTokenDTO);
    }
}
