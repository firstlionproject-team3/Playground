package org.example.playground.domain.comment.exception;

import org.example.playground.global.exception.BusinessException;
import org.example.playground.global.exception.ErrorCode;

public class CommentException extends BusinessException {

    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }

}
