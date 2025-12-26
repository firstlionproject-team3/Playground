package org.example.playground.global.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.debug("User: {} \n User name: {} loaded", oAuth2User.getAttributes(),oAuth2User.getName());

        //플랫폼 구분 변수
        String provider = userRequest.getClientRegistration().getRegistrationId();

        //TODO: 다른 플랫폼의 경우 id 로 안 넘어오는 경우에는 어떻게 해결할지. -> 플랫폼 분기
        String providerId = oAuth2User.getAttribute("id");

        //기존 OAuth2에 담기지 않은 정보 포함
        try{
            //검증 로직
            validProviderAndProviderId(provider, providerId);

            CustomOAuth2User customOAuth2User = CustomOAuth2User.create(provider, providerId, oAuth2User.getAttributes());
            log.info("customOAuth2User's getName() = {}", customOAuth2User.getName());
            return customOAuth2User;

        } catch (IllegalArgumentException e) {
            OAuth2Error providerError = new OAuth2Error("invalid_provider_error",e.getMessage(),null);
            throw new OAuth2AuthenticationException(providerError);
        }

    }

    private void validProviderAndProviderId(String provider, String providerId) {
        if (provider == null || providerId == null) {
            throw new IllegalArgumentException("provider, providerId 는 필수입니다.");
        }
    }
}
