package org.example.playground.global.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // 🎯 실패 이유별로 다른 메시지
        String errorMessage = determineErrorMessage(exception);

        String targetUrl = UriComponentsBuilder.fromUriString("/login")
                .queryParam("error", "social")
                .queryParam("message", errorMessage)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineErrorMessage(AuthenticationException exception) {
        if (exception instanceof OAuth2AuthenticationException) {
            String errorCode = ((OAuth2AuthenticationException) exception).getError().getErrorCode();

            return switch (errorCode) {
                case "access_denied" -> "소셜 로그인 권한을 거부하셨습니다.";
                case "server_error" -> "소셜 로그인 서버에 일시적인 오류가 발생했습니다.";
                case "temporarily_unavailable" -> "소셜 로그인 서비스를 일시적으로 사용할 수 없습니다.";
                default -> "소셜 로그인에 실패했습니다. 다시 시도해주세요.";
            };
        }

        return "로그인 중 오류가 발생했습니다.";

    }
}
