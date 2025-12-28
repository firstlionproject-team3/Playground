package org.example.playground.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
//successHandler에서 받아올 정보를 담는 그릇
public class OAuth2UserInfo {
    private String name;
    private String email;
    private String provider;
    private String providerId;
}
