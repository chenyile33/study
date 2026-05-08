package com.example.common.web.exception;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.common.core.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Iterator;

/**
 * 统一处理常见 Web 异常，并保持 Result 返回结构稳定。
 *
 * <p>这个类放在 common 中，但只有应用显式启用 common-web 后才会注册到 Spring 容器。</p>
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return badRequest(resolveBindingMessage(exception.getBindingResult()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException exception) {
        return badRequest(resolveBindingMessage(exception.getBindingResult()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException exception) {
        return badRequest(resolveConstraintMessage(exception));
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

    private ResponseEntity<Result<Void>> badRequest(String message) {
        return ResponseEntity.badRequest()
                .body(Result.fail(CommonErrorCode.PARAM_ERROR.getCode(), message));
    }

    private String resolveBindingMessage(BindingResult bindingResult) {
        if (bindingResult == null || !bindingResult.hasErrors()) {
            return CommonErrorCode.PARAM_ERROR.getMessage();
        }

        if (bindingResult.hasFieldErrors()) {
            FieldError fieldError = bindingResult.getFieldErrors().get(0);
            if (fieldError.isBindingFailure()) {
                return fieldError.getField() + "参数类型错误";
            }

            String defaultMessage = fieldError.getDefaultMessage();
            return defaultMessage == null || defaultMessage.isBlank()
                    ? fieldError.getField() + "参数错误"
                    : defaultMessage;
        }

        String defaultMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
        return defaultMessage == null || defaultMessage.isBlank()
                ? CommonErrorCode.PARAM_ERROR.getMessage()
                : defaultMessage;
    }

    private String resolveConstraintMessage(ConstraintViolationException exception) {
        Iterator<ConstraintViolation<?>> iterator = exception.getConstraintViolations().iterator();
        if (!iterator.hasNext()) {
            return CommonErrorCode.PARAM_ERROR.getMessage();
        }

        String message = iterator.next().getMessage();
        return message == null || message.isBlank() ? CommonErrorCode.PARAM_ERROR.getMessage() : message;
    }
}
