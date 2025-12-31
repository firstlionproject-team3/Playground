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

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2SuccessHandler 테스트")
class OAuth2SuccessHandlerTest {

    @Mock
    private TempCodeStore tempCodeStore;

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
        when(mockUser.getUserId()).thenReturn(1L);
        when(mockUser.getRoles()).thenReturn(List.of("ROLE_USER"));
        when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    @DisplayName("로그인 성공 - 임시 코드 생성하고 리다이렉트")
    void onAuthenticationSuccess_Success() throws Exception {
        // given
        String expectedCode = "temp-code-123";
        when(tempCodeStore.createCode(1L, List.of("ROLE_USER")))
                .thenReturn(expectedCode);

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        // 1. tempCodeStore.createCode가 올바른 파라미터로 호출되었는지 확인
        verify(tempCodeStore, times(1))
                .createCode(1L, List.of("ROLE_USER") );

        // 2. 리다이렉트 URL 검증 - MockHttpServletResponse 사용!
        String redirectUrl = response.getRedirectedUrl();
        assertThat(redirectUrl).isNotNull();
        assertThat(redirectUrl).contains("http://localhost:8080/test/oauth/verify-code");
        assertThat(redirectUrl).contains("code=" + expectedCode);
    }

    @Test
    @DisplayName("로그인 성공 - 정확한 콜백 URL 형식 검증")
    void onAuthenticationSuccess_RedirectUrlFormat() throws Exception {
        // given
        String expectedCode = "abc123xyz";
        when(tempCodeStore.createCode(anyLong(), anyList()))
                .thenReturn(expectedCode);

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        String redirectUrl = response.getRedirectedUrl();

        // URL 형식 상세 검증
        assertThat(redirectUrl)
                .startsWith("http://localhost:8080/test/oauth/verify-code?code=")
                .endsWith(expectedCode);

        // HTTP 상태 코드 확인 (302 리다이렉트)
        assertThat(response.getStatus()).isEqualTo(302);
    }

    @Test
    @DisplayName("여러 권한을 가진 사용자 - 모든 권한이 코드에 포함")
    void onAuthenticationSuccess_MultipleRoles() throws Exception {
        // given
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        when(mockUser.getUserId()).thenReturn(2L);
        when(mockUser.getRoles()).thenReturn(roles);
        when(tempCodeStore.createCode(2L, roles))
                .thenReturn("admin-code-456");

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(tempCodeStore, times(1)).createCode(2L, roles );

        String redirectUrl = response.getRedirectedUrl();
        assertThat(redirectUrl).contains("code=admin-code-456");
    }

    @Test
    @DisplayName("코드 생성 실패시 - 예외 전파")
    void onAuthenticationSuccess_CodeGenerationFails() {
        // given
        when(tempCodeStore.createCode(anyLong(), anyList()))
                .thenThrow(new RuntimeException("코드 생성 실패"));

        // when & then
        assertThatThrownBy(() ->
                successHandler.onAuthenticationSuccess(request, response, authentication)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("코드 생성 실패");

        // 리다이렉트가 발생하지 않았는지 확인
        assertThat(response.getRedirectedUrl()).isNull();
    }

    @Test
    @DisplayName("roles가 빈 리스트인 경우에도 정상 처리")
    void onAuthenticationSuccess_EmptyRoles() throws Exception {
        // given
        List<String> emptyRoles = List.of();
        when(mockUser.getRoles()).thenReturn(emptyRoles);
        when(tempCodeStore.createCode(1L, emptyRoles))
                .thenReturn("code-no-roles");

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(tempCodeStore).createCode(1L, emptyRoles);
        assertThat(response.getRedirectedUrl()).contains("code=code-no-roles");
    }
}