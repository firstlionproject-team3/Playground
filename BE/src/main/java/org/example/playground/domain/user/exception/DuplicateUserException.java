package org.example.playground.domain.user.exception;

import org.example.playground.global.exception.BusinessException;

import static org.example.playground.domain.user.exception.UserErrorCode.USER_DUPLICATE;

public class DuplicateUserException extends BusinessException {
    public DuplicateUserException(String message) {
        super(USER_DUPLICATE);
    }
}
