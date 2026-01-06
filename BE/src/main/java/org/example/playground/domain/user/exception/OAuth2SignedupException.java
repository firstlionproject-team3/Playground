package org.example.playground.domain.user.exception;

import org.example.playground.global.exception.BusinessException;

import static org.example.playground.domain.user.exception.UserErrorCode.OAUTH2_SIGNED_UP;

public class OAuth2SignedupException extends BusinessException {
    public OAuth2SignedupException(String message) {
        super(OAUTH2_SIGNED_UP);
    }
}
