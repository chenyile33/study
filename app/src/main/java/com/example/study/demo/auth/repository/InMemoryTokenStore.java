package com.example.study.demo.auth.repository;

import com.example.common.core.auth.AuthPrincipal;
import com.example.study.demo.auth.domain.StoredToken;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 内存 token 存储，演示 opaque token 的服务端状态。
 */
@Repository
public class InMemoryTokenStore {

    /**
     * key 是 token 原文，value 是服务端保存的登录状态。
     */
    private final ConcurrentMap<String, StoredToken> tokens = new ConcurrentHashMap<>();

    public StoredToken create(AuthPrincipal principal, Duration ttl) {
        String token = UUID.randomUUID().toString().replace("-", "");
        StoredToken storedToken = new StoredToken(token, principal, LocalDateTime.now().plus(ttl));
        tokens.put(token, storedToken);
        return storedToken;
    }

    public Optional<StoredToken> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        StoredToken storedToken = tokens.get(token.trim());
        if (storedToken == null) {
            return Optional.empty();
        }

        // 过期 token 在读取时顺手清理，避免内存里长期堆积无效状态。
        if (storedToken.isExpired(LocalDateTime.now())) {
            tokens.remove(storedToken.getToken());
            return Optional.empty();
        }
        return Optional.of(storedToken);
    }

    public void remove(String token) {
        if (token != null && !token.isBlank()) {
            tokens.remove(token.trim());
        }
    }
}
