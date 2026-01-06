package org.example.playground.global.security.jwt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 자바객체를 JSON으로, JSON을 자바객체로
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");

        // null이면 필터에서 setAttribute 해준 예외가 아닌 것임.
        if (exception == null) {
            log.error("Commence Occurred :: " + authException.getMessage());
        }

        // HTTP 401 상태코드와 JSON body로 응답을 보낸다.
        JwtExceptionCode code = JwtExceptionCode.findByCode(exception);

        // 에러코드, 에러메시지를 JSON으로 변환
        Map<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", code.getMessage());
        errorInfo.put("code", code.getCode());
        String responseJson = objectMapper.writeValueAsString(errorInfo);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().print(responseJson);
    }
}
