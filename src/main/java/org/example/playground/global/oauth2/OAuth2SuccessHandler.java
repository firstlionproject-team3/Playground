package org.example.playground.global.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.global.oauth2.exception.TemporaryCodeGenerationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TempCodeStore tempCodeStore;
    //TODO: 테스트 url 프론트와 연결시 수정 할 것, 토큰 발급쪽 완성되면 반드시 교체해야함.
    @Value("${playground.oauth.callback-url:http://localhost:8080/test/oauth/verify-code}")
    private String callBackUrl;
    @Value("${playground.oauth.fail.url:/test/oauth/fail}")
    private String errorUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth user in successHandler");

        // 1. 유저 정보 획득
        CustomOAuth2User socialUser = (CustomOAuth2User) authentication.getPrincipal();
        try {
            // 임시 코드 생성
            String code = tempCodeStore.createCode(socialUser.getUserId(), socialUser.getRoles());

            String successUrl = getSuccessUrl(code);

            //리다이렉트 url
            log.info("Redirecting to callback, code = {}", code);
            getRedirectStrategy().sendRedirect(request, response, successUrl);
        } catch (TemporaryCodeGenerationException e) {
            //글로벌 어드바이스에서 잡지 못하기 때문에 따로 설정
            log.error("Code generation failed: {}", e.getMessage());
            redirectToError(request, response, "temporary_code_generation_failed");
        } catch (Exception e) {

            log.error("Unexpected error for userId: {}", socialUser.getUserId(), e);
            redirectToError(request, response, "server_error");
        }
    }



    private String getSuccessUrl(String code) {
        return UriComponentsBuilder
                .fromUriString(callBackUrl)
                .queryParam("code", code)
                .build()
                .toUriString();
    }

    private void redirectToError(HttpServletRequest request, HttpServletResponse response, String code) throws IOException {

        String url = UriComponentsBuilder
                .fromUriString(errorUrl)
                .queryParam("error", "error_social_login")
                .queryParam("code", code)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, url);
    }

}
