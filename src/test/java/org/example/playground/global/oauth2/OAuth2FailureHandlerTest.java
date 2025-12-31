package org.example.playground.global.oauth2;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.RedirectStrategy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2FailureHandler 테스트")
class OAuth2FailureHandlerTest {

    @InjectMocks
    private OAuth2FailureHandler failureHandler;

    @Mock
    private RedirectStrategy redirectStrategy;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // RedirectStrategy 주입
        failureHandler.setRedirectStrategy(redirectStrategy);
    }

    @Test
    @DisplayName("커스텀 OAuth 에러 - 에러 코드를 포함한 리다이렉트")
    void onAuthenticationFailure_CustomOAuthError() throws Exception {
        // given
        // OAuthErrorCode에 정의된 에러코드 사용 (실제 코드에 맞게 수정 필요)
        OAuth2Error error = new OAuth2Error("UNSUPPORTED_PROVIDER");
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(error);

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        // 1. 리다이렉트가 호출되었는지 확인
        verify(redirectStrategy, times(1)).sendRedirect(
                eq(request),
                eq(response),
                anyString()
        );

        // 2. URL에 error=social과 code 파라미터가 포함되었는지 확인
        verify(redirectStrategy).sendRedirect(
                any(),
                any(),
                argThat(url ->
                        url.contains("/login") &&
                                url.contains("error=social") &&
                                url.contains("code=")
                )
        );
    }

    @Test
    @DisplayName("OAuth가 아닌 일반 인증 에러 - 기본 에러 메시지로 리다이렉트")
    void onAuthenticationFailure_NonOAuthError() throws Exception {
        // given
        AuthenticationException exception = new BadCredentialsException("잘못된 인증 정보");

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        verify(redirectStrategy).sendRedirect(
                any(),
                any(),
                argThat(url ->
                        url.contains("로그인 중 오류가 발생했습니다") ||
                                url.contains("error=social")
                )
        );
    }

    @Test
    @DisplayName("알 수 없는 OAuth 에러 코드 - 원본 에러코드 그대로 전달")
    void onAuthenticationFailure_UnknownErrorCode() throws Exception {
        // given
        OAuth2Error error = new OAuth2Error("UNKNOWN_ERROR_CODE");
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(error);

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        verify(redirectStrategy).sendRedirect(
                any(),
                any(),
                argThat(url -> url.contains("code=UNKNOWN_ERROR_CODE"))
        );
    }

    @Test
    @DisplayName("리다이렉트 URL 형식 검증 - /login으로 시작")
    void onAuthenticationFailure_RedirectUrlFormat() throws Exception {
        // given
        OAuth2Error error = new OAuth2Error("TEST_ERROR");
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(error);

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        verify(redirectStrategy).sendRedirect(
                any(),
                any(),
                argThat(url -> {
                    assertThat(url).startsWith("/login");
                    assertThat(url).contains("error=social");
                    assertThat(url).contains("code=");
                    return true;
                })
        );
    }

    @Test
    @DisplayName("여러 종류의 OAuth 에러 코드 처리")
    void onAuthenticationFailure_VariousErrorCodes() throws Exception {
        // OAuthErrorCode enum에 정의된 다양한 에러들을 테스트
        // 실제 OAuthErrorCode 클래스의 값들에 맞게 수정 필요

        String[] errorCodes = {
                "UNSUPPORTED_PROVIDER",
                "PROVIDER_ID_MAPPING_FAILED",
                "ATTRIBUTES_MAPPING_FAILED"
        };

        for (String errorCode : errorCodes) {
            // given
            OAuth2Error error = new OAuth2Error(errorCode);
            OAuth2AuthenticationException exception = new OAuth2AuthenticationException(error);

            // when
            failureHandler.onAuthenticationFailure(request, response, exception);

            // then
            verify(redirectStrategy, atLeastOnce()).sendRedirect(
                    any(),
                    any(),
                    argThat(url -> url.contains("code="))
            );
        }
    }
}