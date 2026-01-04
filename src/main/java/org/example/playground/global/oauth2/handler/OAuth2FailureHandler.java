package org.example.playground.global.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.global.oauth2.exception.OAuthErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String ERROR_SOCIAL = "error_social_login";
    @Value("${playground.oauth.fail.url:/test/oauth/fail}")
    private String failUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.info("user in failureHandler");

        String errorCode = determineErrorCode(exception);
        String targetUrl = UriComponentsBuilder.fromUriString(failUrl)
                .queryParam("error", ERROR_SOCIAL)
                .queryParam("code", errorCode)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineErrorCode(AuthenticationException exception) {

        if (!(exception instanceof OAuth2AuthenticationException oauthEx)) {
            return "authentication_error";
        }

        OAuth2Error error = oauthEx.getError();
        if (error == null || error.getErrorCode() == null) {
              return "oauth_error";
          }
        String errorCode = error.getErrorCode();

        OAuthErrorCode customCode = OAuthErrorCode.from(errorCode);

        //커스텀 에러
        if (customCode != null) {
            return customCode.getCode();
        }

        //기본적으로 규정된 에러
        return errorCode;
    }
}
