package org.example.playground.domain.report.exception;

import org.example.playground.global.exception.BusinessException;
import org.example.playground.global.exception.ErrorCode;

public class ReportException extends BusinessException {

    public ReportException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ReportException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
