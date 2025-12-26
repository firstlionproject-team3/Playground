package org.example.playground.global.oauth2;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomOAuth2User implements OAuth2User {

    private final String provider;
    private final String providerId;
    private final Map<String, Object> attributes;


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return provider + ": " + providerId;
    }

    public static CustomOAuth2User create(String provider, String providerId, Map<String, Object> attributes) {

        return CustomOAuth2User.builder()
                .provider(provider)
                .providerId(providerId)
                .attributes(attributes)
                .build();
    }



}
