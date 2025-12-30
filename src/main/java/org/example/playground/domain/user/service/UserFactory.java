package org.example.playground.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.OAuth2UserInfo;
import org.example.playground.domain.user.dto.UserRegisterRequestDTO;
import org.example.playground.domain.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class UserFactory {
    private final PasswordEncoder passwordEncoder;

    // 유니크 name 자동 생성 (짧고 충돌확률 낮게)
    private static final int NAME_LEN = 10;
    private static final String NAME_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    public User createLocal(UserRegisterRequestDTO dto, String nickname) {
        return User.createLocalUser(
                dto,
                nickname,
                passwordEncoder.encode(dto.getPassword())
        );
    }

    public User createOAuth(OAuth2UserInfo info, String nickname) {
        // 소셜 유저는 password 로그인에 쓰지 않으므로 더미 생성 (NOT NULL 만족용)
        String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        String loginId = info.getProvider() + ":" + info.getProviderId();

        return User.createOAuthUser(
                info,
                loginId,
                nickname,
                dummyPassword);
    }

    public String newAutoNickname() {
        StringBuilder sb = new StringBuilder("익명의 개발자_");
        for (int i = 0; i < NAME_LEN; i++) {
            int idx = ThreadLocalRandom.current().nextInt(NAME_CHARS.length());
            sb.append(NAME_CHARS.charAt(idx));
        }
        return sb.toString();
    }
}
