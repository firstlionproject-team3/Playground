package org.example.playground.global.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.domain.user.dto.OAuth2UserInfo;
import org.example.playground.domain.user.dto.SecurityResponseForJWT;
import org.example.playground.domain.user.service.UserService;
import org.example.playground.global.oauth2.exception.OAuthAttributesMappingException;
import org.example.playground.global.oauth2.exception.OAuthErrorCode;
import org.example.playground.global.oauth2.exception.OAuthProviderIdMappingException;
import org.example.playground.global.oauth2.exception.OAuthUnsupportedProviderException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private static final String NAVER = "naver";
    private static final String GITHUB = "github";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        try {
            log.info("user in loadUser method");
            OAuth2User oAuth2User = super.loadUser(userRequest);

            log.debug("User: {} \n User name: {} loaded", oAuth2User.getAttributes(), oAuth2User.getName());
            //플랫폼 구분
            String provider = userRequest.getClientRegistration().getRegistrationId();
            //소셜유저 매핑
            CustomOAuth2User socialUserInfo = mapToOAuth2User(provider, oAuth2User);

            //playground db 접근=======================
            String nullableEmail = socialUserInfo.getAttribute("email");

            OAuth2UserInfo build = OAuth2UserInfo.builder()
                    .email(nullableEmail)
                    .providerId(socialUserInfo.getProviderId())
                    .provider(socialUserInfo.getProvider())
                    .build();
            
            //playground db 유저 정보 획득
            SecurityResponseForJWT securityResponseForJWT = userService.handleOAuth2Login(build);
            Long userId = securityResponseForJWT.getId();
            List<String> roles = securityResponseForJWT.getRoles();
            CustomOAuth2User user = CustomOAuth2User.attachUser(socialUserInfo, userId, roles);

            //========================

            log.info("OAuth user login success provider={}", user.getProvider());
            log.info("OAuth user login success user={}", user);
            log.debug("OAuth user's provider={}, providerId={}", user.getProvider(), user.getProviderId());
            return user;
        } catch (OAuth2AuthenticationException e) {
            log.error("OAuth authentication failed: {}", e.getError().getDescription());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during OAuth login", e);

            throw new OAuth2AuthenticationException(
                    new OAuth2Error("oauth_process_error",
                            "Failed to process OAuth login: " + e.getMessage(),
                            null)
            );
        }


    }

    private CustomOAuth2User mapToOAuth2User(String provider, OAuth2User oAuth2User) {
        String providerId = null;
        //소셜 플랫폼 별 정보 매핑
        if (NAVER.equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            extractAttributesOrThrow(response);
            providerId = String.valueOf(response.get("id"));
            extractProviderIdOrThrow(providerId);
            return CustomOAuth2User.create(NAVER, providerId, response);
        }

        if (GITHUB.equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes();
            extractAttributesOrThrow(response);
            providerId = String.valueOf(response.get("id"));
            extractProviderIdOrThrow(providerId);
            return CustomOAuth2User.create(GITHUB, providerId, response);
        }

        throw new OAuthUnsupportedProviderException(OAuthErrorCode.UNSUPPORTED_PROVIDER);
    }

    private void extractProviderIdOrThrow(String id) {
        if (id == null || "null".equals(id))
            throw new OAuthProviderIdMappingException(OAuthErrorCode.PROVIDER_ID_MAPPING_FAILED);
    }

    private void extractAttributesOrThrow(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty())
            throw new OAuthAttributesMappingException(OAuthErrorCode.ATTRIBUTES_MAPPING_FAILED);
    }

}
