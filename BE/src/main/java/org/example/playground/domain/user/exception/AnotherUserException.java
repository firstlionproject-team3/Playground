package org.example.playground.domain.user.exception;

import org.example.playground.global.exception.BusinessException;

import static org.example.playground.domain.user.exception.UserErrorCode.ANOTHER_USER_FORBIDDEN;

public class AnotherUserException extends BusinessException {
    public AnotherUserException(String message) {
        super(ANOTHER_USER_FORBIDDEN);
    }
}
