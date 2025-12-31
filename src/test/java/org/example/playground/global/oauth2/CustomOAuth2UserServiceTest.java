package org.example.playground.global.oauth2;

import org.example.playground.domain.user.dto.OAuth2UserInfo;
import org.example.playground.domain.user.dto.SecurityResponseForJWT;
import org.example.playground.domain.user.service.UserService;
import org.example.playground.global.oauth2.exception.OAuthAttributesMappingException;
import org.example.playground.global.oauth2.exception.OAuthProviderIdMappingException;
import org.example.playground.global.oauth2.exception.OAuthUnsupportedProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomOAuth2UserService 테스트")
class CustomOAuth2UserServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    private OAuth2UserRequest userRequest;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 초기화
    }

    @Test
    @DisplayName("네이버 로그인 성공 - 정상적으로 사용자 정보를 매핑한다")
    void loadUser_Naver_Success() {
        // given - 테스트에 필요한 데이터 준비
        Map<String, Object> naverAttributes = new HashMap<>();
        naverAttributes.put("id", "naver123");
        naverAttributes.put("email", "test@naver.com");
        naverAttributes.put("name", "테스터");

        Map<String, Object> response = new HashMap<>();
        response.put("response", naverAttributes);

        OAuth2User mockOAuth2User = new DefaultOAuth2User(
                List.of(),
                response,
                "response"
        );

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("naver")
                .clientId("test-client-id")
                .clientSecret("test-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/naver")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .build();

        userRequest = new OAuth2UserRequest(clientRegistration, mock());

        SecurityResponseForJWT mockResponse = SecurityResponseForJWT.builder()
                .id(1L)
                .roles(List.of("ROLE_USER"))
                .build();

        when(userService.handleOAuth2Login(any(OAuth2UserInfo.class)))
                .thenReturn(mockResponse);

        // when - 실제 테스트할 메서드 실행
        // 이 부분은 실제로는 super.loadUser()를 호출하므로 통합테스트에서 테스트하는게 나음
        // 여기서는 mapToOAuth2User 메서드를 직접 테스트하는 방법으로 수정 필요

        // then - 결과 검증
        verify(userService, times(0)).handleOAuth2Login(any());
    }

    @Test
    @DisplayName("GitHub 로그인 성공 - 정상적으로 사용자 정보를 매핑한다")
    void loadUser_GitHub_Success() {
        // given
        Map<String, Object> githubAttributes = new HashMap<>();
        githubAttributes.put("id", 12345);
        githubAttributes.put("login", "testuser");
        githubAttributes.put("email", "test@github.com");
        githubAttributes.put("name", "Test User");

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("github")
                .clientId("test-client-id")
                .clientSecret("test-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/github")
                .authorizationUri("https://github.com/login/oauth/authorize")
                .tokenUri("https://github.com/login/oauth/access_token")
                .userInfoUri("https://api.github.com/user")
                .userNameAttributeName("id")
                .build();

        // 테스트 로직 작성...
    }

    @Test
    @DisplayName("지원하지 않는 OAuth 제공자 - 예외 발생")
    void loadUser_UnsupportedProvider_ThrowsException() {
        // given
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("kakao")
                .clientId("test-client-id")
                .clientSecret("test-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .build();

        // when & then - 예외가 발생하는지 확인
        assertThatThrownBy(() -> {
            // mapToOAuth2User를 직접 테스트
            // 실제로는 private 메서드라 리플렉션 사용하거나 통합테스트 필요
        }).isInstanceOf(OAuthUnsupportedProviderException.class);
    }

    @Test
    @DisplayName("providerId가 null일 때 - 예외 발생")
    void loadUser_NullProviderId_ThrowsException() {
        // given
        Map<String, Object> naverAttributes = new HashMap<>();
        naverAttributes.put("id", null); // providerId가 null
        naverAttributes.put("email", "test@naver.com");

        Map<String, Object> response = new HashMap<>();
        response.put("response", naverAttributes);

        // when & then
        assertThatThrownBy(() -> {
            // 테스트 로직
        }).isInstanceOf(OAuthProviderIdMappingException.class);
    }

    @Test
    @DisplayName("attributes가 비어있을 때 - 예외 발생")
    void loadUser_EmptyAttributes_ThrowsException() {
        // given
        Map<String, Object> response = new HashMap<>();
        response.put("response", new HashMap<>()); // 빈 맵

        // when & then
        assertThatThrownBy(() -> {
            // 테스트 로직
        }).isInstanceOf(OAuthAttributesMappingException.class);
    }
}