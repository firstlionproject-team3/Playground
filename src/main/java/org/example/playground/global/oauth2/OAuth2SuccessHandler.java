package org.example.playground.global.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TempCodeStore tempCodeStore;
    //TODO: 테스트 url 프론트와 연결시 수정 할 것
    private final String callBackUrl = "http://localhost:8080/test/oauth/verify-code?code=";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth user in successHandler");

        // 1. 유저 정보 획득
        CustomOAuth2User socialUser = (CustomOAuth2User) authentication.getPrincipal();

        // 임시 코드 생성
        String code = tempCodeStore.createCode(socialUser.getUserId(),socialUser.getRoles());

        //리다이렉트 url
        getRedirectStrategy().sendRedirect(request, response, callBackUrl + code);

    }

}
