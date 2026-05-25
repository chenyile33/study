package com.example.study.config.security;

import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Spring Security 过滤器链里的 401/403 JSON 响应入口。
 *
 * <p>过滤器链异常不会进入 common 的 ControllerAdvice；
 * @PreAuthorize 这类方法级异常又可能从 MVC 调用链抛出，所以这里统一接住两种入口。</p>
 */
@Component
@RestControllerAdvice
public class SecurityJsonExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String DEFAULT_UNAUTHORIZED_MESSAGE = "未登录";
    private static final String DEFAULT_FORBIDDEN_MESSAGE = "操作权限不足";

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        write(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                AuthErrorCode.UNAUTHORIZED,
                resolveUnauthorizedMessage(authException)
        );
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        write(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                AuthErrorCode.FORBIDDEN,
                resolveForbiddenMessage(accessDeniedException)
        );
    }

    /**
     * @PreAuthorize 抛出的 AccessDeniedException 会进入 MVC 异常处理链，而不是过滤器链的 AccessDeniedHandler。
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleMethodAccessDenied(AccessDeniedException accessDeniedException) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.fail(AuthErrorCode.FORBIDDEN.getCode(), resolveForbiddenMessage(accessDeniedException)));
    }

    private void write(HttpServletResponse response, int status, AuthErrorCode errorCode,
                       String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(JSON_CONTENT_TYPE);
        objectMapper.writeValue(response.getWriter(), Result.fail(errorCode.getCode(), message));
    }

    private String resolveUnauthorizedMessage(AuthenticationException exception) {
        AuthException authException = findAuthException(exception);
        if (authException != null && hasText(authException.getMessage())) {
            return authException.getMessage();
        }
        // 只有我们主动包装的 token 校验失败才透出消息，避免暴露 Spring Security 默认英文错误。
        if (exception instanceof BadCredentialsException && hasText(exception.getMessage())) {
            return exception.getMessage();
        }
        return DEFAULT_UNAUTHORIZED_MESSAGE;
    }

    private String resolveForbiddenMessage(AccessDeniedException exception) {
        AuthException authException = findAuthException(exception);
        if (authException != null && hasText(authException.getMessage())) {
            return authException.getMessage();
        }
        return DEFAULT_FORBIDDEN_MESSAGE;
    }

    private AuthException findAuthException(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof AuthException authException) {
                return authException;
            }
            current = current.getCause();
        }
        return null;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
