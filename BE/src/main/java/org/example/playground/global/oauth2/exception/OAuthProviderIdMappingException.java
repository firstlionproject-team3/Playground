package org.example.playground.global.oauth2.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class OAuthProviderIdMappingException extends OAuth2AuthenticationException {

    public OAuthProviderIdMappingException(OAuthErrorCode errorCode) {
        super(new OAuth2Error(
                errorCode.getCode(),
                errorCode.getDescription(),
                errorCode.getUri())
        );

    }
}
