package org.example.playground.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest request
    ) {
        ErrorCode ec = e.getErrorCode();
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(ec.getStatus().value())
                .error(ec.getStatus().getReasonPhrase())
                .code(ec.getCode())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(ec.getStatus()).body(body);
    }

    // 예상하지 못한 예외: 내부 로그는 상세, 클라이언트 메시지는 단순
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request
    ) {
        log.error("Unhandled exception", e);

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(500)
                .error("Internal Server Error")
                .code("INTERNAL_ERROR")
                .message("서버 오류가 발생했습니다.")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(500).body(body);
    }
}