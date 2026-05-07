package com.example.common.core.util;

import com.example.common.core.error.ErrorCode;
import com.example.common.core.exception.BusinessException;

import java.util.Collection;

public final class AssertUtils {

    private AssertUtils() {
    }

    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode, String message) {
        if (!expression) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void notNull(Object value, ErrorCode errorCode) {
        if (value == null) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notNull(Object value, ErrorCode errorCode, String message) {
        if (value == null) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void hasText(String value, ErrorCode errorCode) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(errorCode);
        }
    }

    public static void hasText(String value, ErrorCode errorCode, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void notEmpty(Collection<?> value, ErrorCode errorCode) {
        if (value == null || value.isEmpty()) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(Collection<?> value, ErrorCode errorCode, String message) {
        if (value == null || value.isEmpty()) {
            throw new BusinessException(errorCode, message);
        }
    }
}
