package org.example.playground.global.security.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.refreshtoken.dto.AccessAndRefreshTokenDTO;
import org.example.playground.domain.refreshtoken.entity.RefreshToken;
import org.example.playground.domain.refreshtoken.repository.RefreshTokenRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.entity.UserStatus;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.security.auth.exception.LoginFailedException;
import org.example.playground.global.security.jwt.JwtTokenProvider;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.example.playground.global.security.user.dto.LoginRequestDTO;
import org.example.playground.global.util.CookieUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AccessAndRefreshTokenDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByLoginIdAndStatus(loginRequestDTO.getLoginId(), UserStatus.ACTIVE)
                // 로그인 로직이기 때문에 어떤 에러든 로그인 실패 예외를 던지는게 보안상 좋다.
                .orElseThrow(() -> new LoginFailedException("아이디 또는 비밀번호가 틀렸습니다."));
        if (user.isDeleted()) { // 더블 체크
            throw new LoginFailedException("아이디 또는 비밀번호가 틀렸습니다.");
        }
        // 패스워드 검증
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new LoginFailedException("아이디 또는 비밀번호가 틀렸습니다.");
        }

        // 권한 set -> list
        List<String> roles = user.getRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .toList();

        // 토큰 발급
        AccessAndRefreshTokenDTO token = createToken(user.getId(), roles);
        String accessToken = token.getAccessToken();
        TokenDTO refreshToken = token.getRefreshToken();

        return AccessAndRefreshTokenDTO.from(accessToken, refreshToken);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {

        // 리프레시 토큰 찾기
        // 리프레시 토큰을 찾을 수 없다면 null이 반환되지만
        // logout을 한다는건 로그인이 되어있는것이고, 토큰이 쿠키에 들어있다는것이 보장이 된다.
        String refreshToken = CookieUtil.getToken(request, "refreshToken");

        // db에서 리프레시 토큰 제거
        refreshTokenRepository.deleteByToken(refreshToken);

        // 쿠키에 있는 리프레시 토큰 제거
        CookieUtil.deleteRefreshToken(response);
    }

    public AccessAndRefreshTokenDTO createToken(Long id, List<String> roles) {
        TokenDTO accessToken = jwtTokenProvider.createAccessToken(id, roles);
        TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(id);
        Date expiration = refreshToken.getExpiration();

        // RefreshToken DB에 저장
        RefreshToken refreshTokenEntity =
                RefreshToken.from(
                        id,
                        refreshToken.getToken(),
                        expiration);
        refreshTokenRepository.save(refreshTokenEntity);

        return AccessAndRefreshTokenDTO.from(accessToken.getToken(), refreshToken);
    }
}
