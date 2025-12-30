package org.example.playground.global.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.playground.global.oauth2.exception.OAuthErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String ERROR_SOCIAL = "social";
    private static final String LOGIN_URL = "/login";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {


        String errorCode = determineErrorCode(exception);
        String targetUrl = UriComponentsBuilder.fromUriString(LOGIN_URL)
                .queryParam("error", ERROR_SOCIAL)
                .queryParam("code", errorCode)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineErrorCode(AuthenticationException exception) {

        if (!(exception instanceof OAuth2AuthenticationException oauthEx)) {
            return "로그인 중 오류가 발생했습니다.";
        }
        
        OAuth2Error error = oauthEx.getError();
        if (error == null || error.getErrorCode() == null) {
              return "소셜 로그인에 실패했습니다. 다시 시도해주세요.";
          }
        String errorCode = error.getErrorCode();

        OAuthErrorCode customCode = OAuthErrorCode.from(errorCode);

        //커스텀 에러
        if (customCode != null) {
            return customCode.getCode();
        }

        //기타 에러
        return errorCode;
    }
}
