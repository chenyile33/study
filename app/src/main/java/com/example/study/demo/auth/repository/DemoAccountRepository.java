package com.example.study.demo.auth.repository;

import com.example.study.demo.auth.domain.DemoAccount;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 固定账号仓库，用来学习登录流程，不连接数据库。
 */
@Repository
public class DemoAccountRepository {

    private final Map<String, DemoAccount> accounts = Map.of(
            "admin", new DemoAccount("1", "admin", "admin123", List.of("ADMIN", "USER")),
            "alice", new DemoAccount("2", "alice", "alice123", List.of("USER"))
    );

    public Optional<DemoAccount> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(accounts.get(username.trim()));
    }
}
