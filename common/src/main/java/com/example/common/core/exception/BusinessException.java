package com.example.common.core.exception;

import com.example.common.core.error.ErrorCode;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }

    public BusinessException(ErrorCode errorCode, String message) {
        this(errorCode.getCode(), message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        this(errorCode.getCode(), errorCode.getMessage(), cause);
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        this(errorCode.getCode(), message, cause);
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
