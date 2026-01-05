package org.example.playground.global.oauth2.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class OAuthUnsupportedProviderException extends OAuth2AuthenticationException {

    public OAuthUnsupportedProviderException(OAuthErrorCode OAuthErrorCode) {
        super(new OAuth2Error(
                OAuthErrorCode.getCode(),
                OAuthErrorCode.getDescription(),
                OAuthErrorCode.getUri())
        );

    }
}
