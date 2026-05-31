package com.example.study.demo.auth.domain;

import com.example.common.core.auth.AuthPrincipal;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 认证 Demo 的账号聚合，包含登录校验需要的账号信息和权限快照。
 */
@Getter
public class DemoAccount {

    /**
     * 账号唯一标识，登录成功后会放进 AuthPrincipal.principalId。
     */
    private final String id;

    /**
     * 登录名，也作为认证主体展示名称。
     */
    private final String username;

    /**
     * BCrypt 哈希后的密码，不保存明文密码。
     */
    private final String passwordHash;

    /**
     * 登录成功后写入认证主体的角色快照。
     */
    private final List<String> roles;

    /**
     * 登录成功后写入认证主体的权限码快照。
     */
    private final List<String> permissions;

    public DemoAccount(String id, String username, String passwordHash, List<String> roles, List<String> permissions) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = List.copyOf(roles);
        this.permissions = List.copyOf(permissions);
    }

    public AuthPrincipal toPrincipal() {
        return AuthPrincipal.of(id, username, roles, permissions, Map.of("source", "auth-demo"));
    }

}
