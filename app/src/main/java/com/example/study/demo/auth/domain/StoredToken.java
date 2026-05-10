package com.example.study.demo.auth.domain;

import com.example.common.core.auth.AuthPrincipal;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 服务端保存的 opaque token 状态。
 */
@Getter
public class StoredToken {

    /**
     * 随机 token 字符串，客户端只需要原样带回。
     */
    private final String token;

    /**
     * token 对应的认证主体快照。
     */
    private final AuthPrincipal principal;

    /**
     * 过期时间；服务端状态型 token 可以主动判断和删除。
     */
    private final LocalDateTime expiresAt;

    public StoredToken(String token, AuthPrincipal principal, LocalDateTime expiresAt) {
        this.token = token;
        this.principal = principal;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired(LocalDateTime now) {
        return !expiresAt.isAfter(now);
    }

}
