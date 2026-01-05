package org.example.playground.global.oauth2.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.global.oauth2.store.CodeInfo;
import org.example.playground.global.oauth2.store.TempCodeStore;
import org.example.playground.global.security.jwt.JwtTokenProvider;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.example.playground.global.util.CookieUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthTestController {

    private final TempCodeStore tempCodeStore;
    private final JwtTokenProvider  jwtTokenProvider;

    /**
     * 테스트용: 수동으로 임시 코드 생성
     * OAuth 플로우 없이 코드 생성 테스트
     */
    @PostMapping("/create-code")
    public Map<String, Object> createTestCode(@RequestParam Long userId,
                                              @RequestParam List<String> roles) {
        String code = tempCodeStore.createCode(userId,roles);

        log.info("Test code created - userId: {}, roles: {}, code: {}", userId, roles, code);

        return Map.of(
                "success", true,
                "code", code,
                "userId", userId,
                "roles", roles,
                "expiresIn", "5 minutes",
                "message", "Test code created successfully. Use this code to test token exchange."
        );
    }

    /**
     * 테스트용: 코드 검증 (토큰 발급 없이)
     * 코드가 유효한지만 확인
     * successHandler 에서 post 요청을 못 보내기 때문에 GET 요청 수행
     */
    @GetMapping("/verify-code")
    public Map<String, Object> verifyCode(@RequestParam String code,
                                          HttpServletResponse response) {
        CodeInfo codeInfo = tempCodeStore.getCodeInfoAndRemove(code);

        log.info("post verify-code, code: {}, codeInfo: {}", code, codeInfo);
        if (codeInfo == null) {
            return Map.of(
                    "success", false,
                    "message", "Invalid or expired code"
            );
        }
        Long userId = codeInfo.getUserId();
        List<String> roles = codeInfo.getRoles();


        if (userId == null) {
            log.warn("Invalid or expired code: {}", code);
            return Map.of(
                    "success", false,
                    "message", "Invalid or expired code"
            );
        }

        log.info("Code verified and removed - userId: {}", userId);
        TokenDTO accessToken = jwtTokenProvider.createAccessToken(userId, roles);
        TokenDTO refreshToken = jwtTokenProvider.createRefreshToken(userId);

        CookieUtil.addRefreshToken(response,refreshToken);

        return Map.of(
                "success", true,
                "userId", userId,
                "roles", roles,
                "refreshToken", refreshToken.getToken(),
                "accessToken", accessToken.getToken(),
                "message", "Code verified and removed successfully"
        );
    }

    /**
     * 테스트용: 현재 저장된 코드 개수 확인
     * 디버깅용
     */
    @GetMapping("/code-count")
    public Map<String, Object> getCodeCount() {

        int size = tempCodeStore.getSize();

        return Map.of(
                "size" , size,
                "message", "Check server logs for stored codes",
                "note", "Codes are stored in-memory and expire after 5 minutes"
        );
    }
}
