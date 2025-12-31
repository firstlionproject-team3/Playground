package org.example.playground.global.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2SuccessHandler 테스트")
class OAuth2SuccessHandlerTest {

    @Mock
    private TempCodeStore tempCodeStore;

    @Mock
    private RedirectStrategy redirectStrategy;

    @InjectMocks
    private OAuth2SuccessHandler successHandler;

    @Mock
    private Authentication authentication;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private CustomOAuth2User mockUser;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // Mock 사용자 생성
        mockUser = mock(CustomOAuth2User.class);

        // RedirectStrategy 주입
        successHandler.setRedirectStrategy(redirectStrategy);
    }

    @Test
    @DisplayName("로그인 성공 - 임시 코드 생성하고 리다이렉트")
    void onAuthenticationSuccess_Success() throws Exception {
        // given
        String expectedCode = "temp-code-123";
        when(mockUser.getUserId()).thenReturn(1L);
        when(mockUser.getRoles()).thenReturn(List.of("ROLE_USER"));

        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(tempCodeStore.createCode(1L, List.of("ROLE_USER")))
                .thenReturn(expectedCode);

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        // 1. tempCodeStore.createCode가 올바른 파라미터로 호출되었는지 확인
        verify(tempCodeStore, times(1))
                .createCode(1L, List.of("ROLE_USER"));

        // 2. 리다이렉트가 올바른 URL로 호출되었는지 확인
        verify(redirectStrategy, times(1))
                .sendRedirect(
                        eq(request),
                        eq(response),
                        contains("code=" + expectedCode)
                );
    }

    @Test
    @DisplayName("로그인 성공 - 올바른 콜백 URL로 리다이렉트")
    void onAuthenticationSuccess_RedirectToCorrectUrl() throws Exception {
        // given
        String expectedCode = "abc123";
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(tempCodeStore.createCode(anyLong(), anyList()))
                .thenReturn(expectedCode);

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(redirectStrategy).sendRedirect(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class),
                argThat(url -> url.contains("http://localhost:8080/test/oauth/verify-code?code="))
        );
    }

    @Test
    @DisplayName("여러 권한을 가진 사용자 - 모든 권한이 코드에 포함")
    void onAuthenticationSuccess_MultipleRoles() throws Exception {
        // given
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        CustomOAuth2User adminUser = mock(CustomOAuth2User.class);
        when(adminUser.getUserId()).thenReturn(2L);
        when(adminUser.getRoles()).thenReturn(roles);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(tempCodeStore.createCode(2L, roles))
                .thenReturn("admin-code");

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(tempCodeStore).createCode(2L, roles);
    }

    @Test
    @DisplayName("코드 생성 실패시 - 예외 전파")
    void onAuthenticationSuccess_CodeGenerationFails() {
        // given
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(tempCodeStore.createCode(anyLong(), anyList()))
                .thenThrow(new RuntimeException("코드 생성 실패"));

        // when & then
        assertThatThrownBy(() ->
                successHandler.onAuthenticationSuccess(request, response, authentication)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("코드 생성 실패");
    }
}