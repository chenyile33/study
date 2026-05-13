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

    /**
     * Demo 固定账号：这里故意让 admin 和 alice 的角色不同、权限部分重叠，便于验证 403 场景。
     */
    private final Map<String, DemoAccount> accounts = Map.of(
            "admin", new DemoAccount(
                    "1",
                    "admin",
                    "admin123",
                    List.of("ADMIN", "USER"),
                    List.of(
                            "secure:read",
                            "secure:admin",
                            "blog:read",
                            "blog:create",
                            "blog:update",
                            "blog:delete"
                    )
            ),
            "alice", new DemoAccount(
                    "2",
                    "alice",
                    "alice123",
                    List.of("USER"),
                    List.of("secure:read", "blog:read")
            )
    );

    public Optional<DemoAccount> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(accounts.get(username.trim()));
    }
}
