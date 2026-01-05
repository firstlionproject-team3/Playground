package org.example.playground.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        Map<String, Object> details = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                details.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(400)
                .error("Bad Request")
                .code("VALIDATION_ERROR")
                .message("요청 값이 올바르지 않습니다.")
                .path(request.getRequestURI())
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            jakarta.validation.ConstraintViolationException e,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(400)
                .error("Bad Request")
                .code("INVALID_PARAMETER")
                .message("요청 파라미터가 올바르지 않습니다.")
                .path(request.getRequestURI())
                .details(Map.of(
                        "violations", e.getConstraintViolations().stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .toList()
                ))
                .build();

        return ResponseEntity.badRequest().body(body);
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