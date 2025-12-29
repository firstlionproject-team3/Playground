package org.example.playground.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.playground.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
//OAuth2 Handler에서 JWT 만들기 위한 재료를 담은 그릇
public class SecurityResponseForJWT {
    private Long id;
    private List<String> roles;

    public static SecurityResponseForJWT securityResponseFromUser(User user){
        return SecurityResponseForJWT.builder()
                .id(user.getId())
                .roles(user.getRoles().stream()
                        .map(ur -> ur.getRole().getName())
                        .collect(Collectors.toList()))
                .build();
    }
}
