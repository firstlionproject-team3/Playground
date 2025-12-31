package org.example.playground.global.security.login.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.dto.AccessAndRefreshTokenDTO;
import org.example.playground.domain.refreshtoken.entity.RefreshToken;
import org.example.playground.domain.refreshtoken.repository.RefreshTokenRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.security.login.exception.LoginFailedException;
import org.example.playground.global.security.jwt.JwtTokenProvider;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.example.playground.global.security.user.dto.LoginRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AccessAndRefreshTokenDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByLoginId(loginRequestDTO.getLoginId())
                // 로그인 로직이기 때문에 어떤 에러든 로그인 실패 예외를 던지는게 보안상 좋다.
                .orElseThrow(() -> new LoginFailedException("아이디 또는 비밀번호가 틀렸습니다."));

        // 패스워드 검증
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new LoginFailedException("아이디 또는 비밀번호가 틀렸습니다.");
        }

        // 권한 set -> list
        List<String> roles = user.getRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .toList();

        // 토큰 발급
        TokenDTO accessToken = jwtTokenProvider.createAccessToken(user.getId(), roles);
        TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        Date expiration = refreshToken.getExpiration();

        // RefreshToken DB에 저장
        RefreshToken refreshTokenEntity =
                RefreshToken.from(
                        user.getId(),
                        refreshToken.getToken(),
                        expiration);
        refreshTokenRepository.save(refreshTokenEntity);

        return AccessAndRefreshTokenDTO.from(accessToken.getToken(), refreshToken);
    }
}
