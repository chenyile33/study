package com.example.common.core.auth;

import com.example.common.core.error.ErrorCode;
import com.example.common.core.exception.BusinessException;

/**
 * 认证与授权失败时使用的业务异常。
 */
public class AuthException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public AuthException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
