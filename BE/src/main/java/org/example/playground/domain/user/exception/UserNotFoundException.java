package org.example.playground.domain.user.exception;

import org.example.playground.global.exception.BusinessException;

import static org.example.playground.domain.user.exception.UserErrorCode.USER_NOT_FOUND;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super(USER_NOT_FOUND);
    }
}
