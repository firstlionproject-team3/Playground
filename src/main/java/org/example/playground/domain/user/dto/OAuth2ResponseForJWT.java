package org.example.playground.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.entity.UserRole;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class OAuth2ResponseForJWT {
    private Long id;
    private List<String> roles;

    public static OAuth2ResponseForJWT oAuth2ResponseFromUser(User user){
        return OAuth2ResponseForJWT.builder()
                .id(user.getId())
                .roles(user.getRoles().stream()
                        .map(ur -> ur.getRole().getName())
                        .collect(Collectors.toList()))
                .build();
    }
}
