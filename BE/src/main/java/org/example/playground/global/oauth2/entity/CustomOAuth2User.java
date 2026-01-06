package org.example.playground.global.oauth2.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomOAuth2User implements OAuth2User {

    private final String provider;
    private final String providerId;
    private final Map<String, Object> attributes;
    private final List<GrantedAuthority> authorities; //attachUser 에서 생성
    
    private final Long userId;        // 도메인 역할, attachUser 에서 생성
    private final List<String> roles; // 도메인 역할, attachUser 에서 생성


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return provider + ": " + providerId;
    }

    @Override
    public String toString() {
        return "CustomOAuth2User{" +
                "attributes=" + attributes +
                ", provider='" + provider + '\'' +
                ", providerId='" + providerId + '\'' +
                ", userId=" + userId +
                ", authorities=" + authorities +
                '}';
    }

    public static CustomOAuth2User create(String provider, String providerId, Map<String, Object> attributes) {

        return CustomOAuth2User.builder()
                .provider(provider)
                .providerId(providerId)
                .attributes(attributes)
                .build();
    }

    public static CustomOAuth2User attachUser(CustomOAuth2User socialUserInfo, Long userId, List<String> roles) {
        return CustomOAuth2User.builder()
                .provider(socialUserInfo.getProvider())
                .providerId(socialUserInfo.getProviderId())
                .attributes(socialUserInfo.getAttributes())
                .userId(userId)
                .roles(roles)
                .authorities(toAuthorities(roles))
                .build();
    }

    private static List<GrantedAuthority> toAuthorities(List<String> roles) {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

}
