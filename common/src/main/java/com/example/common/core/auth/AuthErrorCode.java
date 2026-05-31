package com.example.common.core.auth;

import com.example.common.core.error.ErrorCode;

/**
 * 认证与授权相关错误码。
 */
public enum AuthErrorCode implements ErrorCode {

    /**
     * 未登录、token 缺失、token 无效或 token 过期。
     */
    UNAUTHORIZED(40100, "unauthorized"),

    /**
     * 已认证，但没有访问当前资源的权限。
     */
    FORBIDDEN(40300, "forbidden");

    private final int code;
    private final String message;

    AuthErrorCode(int code, String message) {
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
