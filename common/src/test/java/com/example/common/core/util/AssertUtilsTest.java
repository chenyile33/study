package com.example.common.core.util;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssertUtilsTest {

    @Test
    void isTrueShouldThrowWhenExpressionIsFalse() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> AssertUtils.isTrue(false, CommonErrorCode.PARAM_ERROR)
        );

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        assertEquals(CommonErrorCode.PARAM_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    void notNullShouldThrowWhenValueIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> AssertUtils.notNull(null, CommonErrorCode.PARAM_ERROR, "value is required")
        );

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        assertEquals("value is required", exception.getMessage());
    }

    @Test
    void hasTextShouldThrowWhenValueIsBlank() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> AssertUtils.hasText("  ", CommonErrorCode.PARAM_ERROR)
        );

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        assertEquals(CommonErrorCode.PARAM_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    void notEmptyShouldThrowWhenCollectionIsEmpty() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> AssertUtils.notEmpty(List.of(), CommonErrorCode.PARAM_ERROR)
        );

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
    }

    @Test
    void assertionsShouldPassWhenValuesAreValid() {
        assertDoesNotThrow(() -> AssertUtils.isTrue(true, CommonErrorCode.PARAM_ERROR));
        assertDoesNotThrow(() -> AssertUtils.notNull("value", CommonErrorCode.PARAM_ERROR));
        assertDoesNotThrow(() -> AssertUtils.hasText("value", CommonErrorCode.PARAM_ERROR));
        assertDoesNotThrow(() -> AssertUtils.notEmpty(List.of("value"), CommonErrorCode.PARAM_ERROR));
    }
}
