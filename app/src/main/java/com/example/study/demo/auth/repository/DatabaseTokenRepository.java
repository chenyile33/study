package com.example.study.demo.auth.repository;

import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.study.demo.auth.domain.StoredToken;
import com.example.study.demo.auth.entity.DemoAuthToken;
import com.example.study.demo.auth.mapper.DemoAuthTokenMapper;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 数据库 token 存储，演示 opaque token 的服务端状态落库。
 */
@Repository
public class DatabaseTokenRepository {

    private final DemoAuthTokenMapper tokenMapper;
    private final DemoAccountRepository accountRepository;

    public DatabaseTokenRepository(DemoAuthTokenMapper tokenMapper, DemoAccountRepository accountRepository) {
        this.tokenMapper = tokenMapper;
        this.accountRepository = accountRepository;
    }

    public StoredToken create(AuthPrincipal principal, Duration ttl) {
        Objects.requireNonNull(principal, "principal must not be null");
        Objects.requireNonNull(ttl, "ttl must not be null");
        // opaque token 只保存随机串；权限信息不固化进 token，方便下次请求读取最新权限。
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiresAt = LocalDateTime.now().plus(ttl);

        DemoAuthToken tokenEntity = new DemoAuthToken();
        tokenEntity.setToken(token);
        tokenEntity.setAccountId(parseAccountId(principal.getPrincipalId()));
        tokenEntity.setExpiresAt(expiresAt);
        tokenEntity.setCreateTime(LocalDateTime.now());
        tokenMapper.insert(tokenEntity);

        return new StoredToken(token, principal, expiresAt);
    }

    public Optional<StoredToken> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        // token 查询走 XML，方便观察“token -> account”的服务端状态恢复入口。
        DemoAuthToken tokenEntity = tokenMapper.selectByToken(token.trim());
        if (tokenEntity == null) {
            return Optional.empty();
        }

        // 过期 token 在读取时顺手删除，避免 token 表长期堆积无效状态。
        if (!tokenEntity.getExpiresAt().isAfter(LocalDateTime.now())) {
            tokenMapper.deleteById(tokenEntity.getToken());
            return Optional.empty();
        }

        return accountRepository.findById(tokenEntity.getAccountId())
                // 重新组装主体，账号被停用或权限被修改后，旧 token 也会感知到最新状态。
                .map(account -> new StoredToken(tokenEntity.getToken(), account.toPrincipal(), tokenEntity.getExpiresAt()));
    }

    public void remove(String token) {
        if (token != null && !token.isBlank()) {
            tokenMapper.deleteById(token.trim());
        }
    }

    private Long parseAccountId(String principalId) {
        try {
            return Long.valueOf(principalId);
        } catch (NumberFormatException exception) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "认证主体ID格式不正确");
        }
    }
}
