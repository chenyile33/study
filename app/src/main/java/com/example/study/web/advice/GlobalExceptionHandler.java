package com.example.study.web.advice;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.common.core.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 统一处理 Web 层异常，保证接口返回结构稳定。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException exception) {
        return Result.fail(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        return Result.fail(CommonErrorCode.PARAM_ERROR.getCode(), exception.getParameterName() + "不能为空");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return Result.fail(CommonErrorCode.PARAM_ERROR.getCode(), exception.getName() + "参数类型错误");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        log.error("未处理的系统异常", exception);
        return Result.fail(CommonErrorCode.SYSTEM_ERROR);
    }
}
