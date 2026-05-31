package com.example.common.core.auth.authorization;

import com.example.common.core.auth.AuthPrincipal;

import java.util.Collection;

/**
 * 授权判断接口。
 *
 * <p>注解只负责声明“需要什么”，真正“当前用户有没有”由这个接口判断。
 * 默认实现会读取 AuthPrincipal 中的角色和权限；真实项目可以替换为查数据库、缓存或远程权限服务。</p>
 */
public interface AuthorizationChecker {

    /**
     * 判断认证主体是否满足角色要求。
     */
    boolean hasRoles(AuthPrincipal principal, Collection<String> roles, AuthorizationMode mode);

    /**
     * 判断认证主体是否满足权限码要求。
     */
    boolean hasPermissions(AuthPrincipal principal, Collection<String> permissions, AuthorizationMode mode);
}
