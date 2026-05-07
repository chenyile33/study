package com.example.common.core.error;

public enum CommonErrorCode implements ErrorCode {

    SUCCESS(0, "success"),
    PARAM_ERROR(40000, "param error"),
    SYSTEM_ERROR(50000, "system error");

    private final int code;
    private final String message;

    CommonErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
