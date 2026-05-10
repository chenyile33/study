package com.example.study.demo.auth.domain;

import lombok.Getter;

import java.util.List;

/**
 * 认证 Demo 的固定账号，只服务当前 app 学习场景。
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
     * 明文密码仅用于学习认证流程，真实项目不能这样保存密码。
     */
    private final String password;

    /**
     * 登录成功后写入认证主体的角色快照。
     */
    private final List<String> roles;

    public DemoAccount(String id, String username, String password, List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = List.copyOf(roles);
    }

    public boolean matchesPassword(String rawPassword) {
        return password.equals(rawPassword);
    }

}
