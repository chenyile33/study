package com.example.common.core.exception;

import com.example.common.core.error.CommonErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BusinessExceptionTest {

    @Test
    void constructorShouldUseErrorCodeValues() {
        BusinessException exception = new BusinessException(CommonErrorCode.PARAM_ERROR);

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        assertEquals(CommonErrorCode.PARAM_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    void constructorShouldSupportCustomMessage() {
        BusinessException exception = new BusinessException(CommonErrorCode.PARAM_ERROR, "name is required");

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        assertEquals("name is required", exception.getMessage());
    }

    @Test
    void constructorShouldSupportCause() {
        RuntimeException cause = new RuntimeException("database timeout");
        BusinessException exception = new BusinessException(CommonErrorCode.SYSTEM_ERROR, "request failed", cause);

        assertEquals(CommonErrorCode.SYSTEM_ERROR.getCode(), exception.getCode());
        assertEquals("request failed", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
