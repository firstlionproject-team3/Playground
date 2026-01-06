package org.example.playground.domain.user.exception;

import org.example.playground.global.exception.BusinessException;
import org.example.playground.global.exception.ErrorCode;

public class UserException extends BusinessException {
    public UserException(ErrorCode code) {
        super(code);
    }
}