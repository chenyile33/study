package com.example.study.demo.auth.dto;

import com.example.common.core.auth.AuthPrincipal;
import com.example.study.demo.auth.domain.StoredToken;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 登录成功后的 token 响应。
 */
@Getter
public class LoginResponse {

    private final String tokenType;
    private final String accessToken;
    private final LocalDateTime expiresAt;
    private final AuthPrincipalResponse principal;

    private LoginResponse(String tokenType, String accessToken, LocalDateTime expiresAt,
                          AuthPrincipalResponse principal) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
        this.principal = principal;
    }

    public static LoginResponse from(StoredToken storedToken) {
        return new LoginResponse(
                "Bearer",
                storedToken.getToken(),
                storedToken.getExpiresAt(),
                AuthPrincipalResponse.from(storedToken.getPrincipal())
        );
    }

    public static LoginResponse bearer(String accessToken, LocalDateTime expiresAt, AuthPrincipal principal) {
        return new LoginResponse(
                "Bearer",
                accessToken,
                expiresAt,
                AuthPrincipalResponse.from(principal)
        );
    }

}
