package org.example.playground.domain.refreshtoken.exception;

import org.example.playground.global.exception.BusinessException;
import org.example.playground.global.exception.ErrorCode;

public class RefreshTokenException extends BusinessException {
    public RefreshTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
