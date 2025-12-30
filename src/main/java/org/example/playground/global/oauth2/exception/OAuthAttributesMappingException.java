package org.example.playground.global.oauth2.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class OAuthAttributesMappingException extends OAuth2AuthenticationException {

    public OAuthAttributesMappingException(OAuthErrorCode errorCode) {
        super(new OAuth2Error(
                errorCode.getCode(),
                errorCode.getDescription(),
                errorCode.getUri())
        );

    }
}
