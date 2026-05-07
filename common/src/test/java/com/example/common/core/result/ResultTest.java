package com.example.common.core.result;

import com.example.common.core.error.CommonErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTest {

    @Test
    void successShouldUseSuccessCodeAndCarryData() {
        Result<String> result = Result.success("ok");

        assertEquals(CommonErrorCode.SUCCESS.getCode(), result.getCode());
        assertEquals(CommonErrorCode.SUCCESS.getMessage(), result.getMessage());
        assertEquals("ok", result.getData());
        assertTrue(result.isSuccess());
    }

    @Test
    void successWithoutDataShouldCarryNullData() {
        Result<Object> result = Result.success();

        assertEquals(CommonErrorCode.SUCCESS.getCode(), result.getCode());
        assertNull(result.getData());
        assertTrue(result.isSuccess());
    }

    @Test
    void failShouldUseGivenCodeAndMessage() {
        Result<Object> result = Result.fail(CommonErrorCode.PARAM_ERROR);

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), result.getCode());
        assertEquals(CommonErrorCode.PARAM_ERROR.getMessage(), result.getMessage());
        assertNull(result.getData());
        assertFalse(result.isSuccess());
    }
}
