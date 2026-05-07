package com.example.study.web.advice;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.common.core.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException exception) {
        HttpStatus status = resolveBusinessStatus(exception);
        return ResponseEntity.status(status).body(Result.fail(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        return ResponseEntity.badRequest()
                .body(Result.fail(CommonErrorCode.PARAM_ERROR.getCode(), exception.getParameterName() + "不能为空"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.badRequest()
                .body(Result.fail(CommonErrorCode.PARAM_ERROR.getCode(), exception.getName() + "参数类型错误"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest()
                .body(Result.fail(CommonErrorCode.PARAM_ERROR.getCode(), "请求体格式错误"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception exception) {
        log.error("未处理的系统异常", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.fail(CommonErrorCode.SYSTEM_ERROR));
    }

    private HttpStatus resolveBusinessStatus(BusinessException exception) {
        if (CommonErrorCode.SYSTEM_ERROR.getCode() == exception.getCode()) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
