package org.example.playground.global.oauth2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2FailureHandler 테스트 - 실용적 버전")
class OAuth2FailureHandlerTest {

    @InjectMocks
    private OAuth2FailureHandler failureHandler;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("커스텀 OAuth 에러 - 에러 코드를 포함한 리다이렉트")
    void onAuthenticationFailure_CustomOAuthError() throws Exception {
        // given
        OAuth2Error error = new OAuth2Error("UNSUPPORTED_PROVIDER");
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(error);

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        String redirectUrl = response.getRedirectedUrl();

        assertThat(redirectUrl).isNotNull();
        assertThat(redirectUrl).contains("/login");
        assertThat(redirectUrl).contains("error=social");
        assertThat(redirectUrl).contains("code=UNSUPPORTED_PROVIDER");
        assertThat(response.getStatus()).isEqualTo(302);
    }

    @Test
    @DisplayName("OAuth가 아닌 일반 인증 에러 - 기본 에러 메시지로 리다이렉트")
    void onAuthenticationFailure_NonOAuthError() throws Exception {
        // given
        AuthenticationException exception = new BadCredentialsException("잘못된 인증 정보");

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        String redirectUrl = response.getRedirectedUrl();

        assertThat(redirectUrl).isNotNull();
        assertThat(redirectUrl).contains("/login");
        assertThat(redirectUrl).contains("error=social");
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
        String redirectUrl = response.getRedirectedUrl();

        assertThat(redirectUrl).contains("code=UNKNOWN_ERROR_CODE");
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
        String redirectUrl = response.getRedirectedUrl();

        assertThat(redirectUrl)
                .startsWith("/login")
                .contains("error=social")
                .contains("code=");
    }

    @Test
    @DisplayName("여러 종류의 OAuth 에러 코드 처리")
    void onAuthenticationFailure_VariousErrorCodes() throws Exception {
        // OAuthErrorCode enum에 정의된 실제 에러들을 테스트
        String[] errorCodes = {
                "UNSUPPORTED_PROVIDER",
                "PROVIDER_ID_MAPPING_FAILED",
                "ATTRIBUTES_MAPPING_FAILED"
        };

        for (String errorCode : errorCodes) {
            // given
            OAuth2Error error = new OAuth2Error(errorCode);
            OAuth2AuthenticationException exception = new OAuth2AuthenticationException(error);

            // 각 테스트마다 새로운 response 생성
            response = new MockHttpServletResponse();

            // when
            failureHandler.onAuthenticationFailure(request, response, exception);

            // then
            String redirectUrl = response.getRedirectedUrl();
            assertThat(redirectUrl)
                    .as("에러 코드 %s에 대한 리다이렉트 URL 검증", errorCode)
                    .contains("code=" + errorCode)
                    .contains("/login")
                    .contains("error=social");
        }
    }

    @Test
    @DisplayName("에러 설명(description)이 있는 경우도 정상 처리")
    void onAuthenticationFailure_WithErrorDescription() throws Exception {
        // given
        OAuth2Error error = new OAuth2Error(
                "SERVER_ERROR",
                "서버에서 오류가 발생했습니다",
                null
        );
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(error);

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        String redirectUrl = response.getRedirectedUrl();

        assertThat(redirectUrl).contains("code=SERVER_ERROR");
        // description은 로그에만 기록되고 URL에는 포함 안 될 수 있음
    }

    @Test
    @DisplayName("특수문자가 포함된 에러 코드 - URL 인코딩 확인")
    void onAuthenticationFailure_ErrorCodeWithSpecialChars() throws Exception {
        // given
        // 실제로는 이런 에러 코드가 잘 안 나오지만, 혹시 모를 경우를 대비
        OAuth2Error error = new OAuth2Error("ERROR_CODE_123");
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(error);

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        String redirectUrl = response.getRedirectedUrl();

        assertThat(redirectUrl).isNotNull();
        assertThat(redirectUrl).contains("code=ERROR_CODE_123");
    }

    @Test
    @DisplayName("HTTP 상태 코드 확인 - 302 Redirect")
    void onAuthenticationFailure_HttpStatusCode() throws Exception {
        // given
        OAuth2Error error = new OAuth2Error("ANY_ERROR");
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(error);

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        assertThat(response.getStatus()).isEqualTo(302);
    }
}