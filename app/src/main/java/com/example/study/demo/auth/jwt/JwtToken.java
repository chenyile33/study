package com.example.study.demo.auth.jwt;

import com.example.common.core.auth.AuthPrincipal;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * JWT 签发结果；JWT 不落库，这里只承载返回给客户端所需的信息。
 */
@Getter
public class JwtToken {

    /**
     * 完整 JWT 字符串，格式是 header.payload.signature。
     */
    private final String token;

    /**
     * 签发 JWT 时使用的认证主体快照。
     */
    private final AuthPrincipal principal;

    /**
     * JWT 过期时间，来自 payload 中的 exp。
     */
    private final LocalDateTime expiresAt;

    public JwtToken(String token, AuthPrincipal principal, LocalDateTime expiresAt) {
        this.token = token;
        this.principal = principal;
        this.expiresAt = expiresAt;
    }
}
