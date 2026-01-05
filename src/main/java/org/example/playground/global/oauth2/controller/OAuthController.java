package org.example.playground.global.oauth2.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.global.oauth2.store.CodeInfo;
import org.example.playground.global.oauth2.store.TempCodeStore;
import org.example.playground.global.security.auth.service.AuthService;
import org.example.playground.global.security.jwt.JwtTokenProvider;
import org.example.playground.global.security.jwt.dto.TokenDTO;
import org.example.playground.global.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final TempCodeStore tempCodeStore;
    private final AuthService authService;


    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> exchangeToken(@RequestParam String code,
                                                             HttpServletResponse response) throws IOException {
        // ... 코드 검증 및 토큰 생성
        CodeInfo codeInfo = tempCodeStore.getCodeInfoAndRemove(code);
        if (codeInfo == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid or expired code"));
        }
        Long userId = codeInfo.getUserId();
        List<String> userRoles = codeInfo.getRoles();

        //토큰 발급...
        authService
        Map<String, String> body = Map.of("accessToken", accessToken.getToken());


        return ResponseEntity.ok(body);
    }



}
