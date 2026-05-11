package com.example.common.core.auth;

import java.util.Collection;
import java.util.Optional;

/**
 * 当前线程的认证上下文。
 *
 * <p>Web 过滤器、消息消费者、异步任务等入口负责写入和清理。</p>
 */
public final class AuthContext {

    /**
     * 每个线程保存自己的认证主体，避免并发请求互相覆盖。
     */
    private static final ThreadLocal<AuthPrincipal> PRINCIPAL_HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static Optional<AuthPrincipal> getPrincipal() {
        return Optional.ofNullable(PRINCIPAL_HOLDER.get());
    }

    /**
     * 获取当前认证主体；不存在时抛出未登录异常。
     */
    public static AuthPrincipal requirePrincipal() {
        return getPrincipal()
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED, "未登录"));
    }

    public static boolean isAuthenticated() {
        return PRINCIPAL_HOLDER.get() != null;
    }

    /**
     * 判断当前认证主体是否拥有指定角色；未登录时返回 false。
     */
    public static boolean hasRole(String role) {
        return getPrincipal()
                .map(principal -> principal.hasRole(role))
                .orElse(false);
    }

    /**
     * 判断当前认证主体是否拥有任意一个指定角色；未登录时返回 false。
     */
    public static boolean hasAnyRole(Collection<String> roles) {
        return getPrincipal()
                .map(principal -> principal.hasAnyRole(roles))
                .orElse(false);
    }

    /**
     * 判断当前认证主体是否拥有指定权限码；未登录时返回 false。
     */
    public static boolean hasPermission(String permission) {
        return getPrincipal()
                .map(principal -> principal.hasPermission(permission))
                .orElse(false);
    }

    /**
     * 判断当前认证主体是否拥有任意一个指定权限码；未登录时返回 false。
     */
    public static boolean hasAnyPermission(Collection<String> permissions) {
        return getPrincipal()
                .map(principal -> principal.hasAnyPermission(permissions))
                .orElse(false);
    }

    /**
     * 设置当前线程的认证主体。传入 null 会清理上下文。
     */
    public static void setPrincipal(AuthPrincipal principal) {
        restore(principal);
    }

    /**
     * 打开一个认证作用域，适合在过滤器、拦截器、消费者等入口中配合 try-with-resources 使用。
     *
     * <p>作用域关闭时会恢复进入前的认证主体。</p>
     */
    public static AuthScope open(AuthPrincipal principal) {
        AuthPrincipal previousPrincipal = PRINCIPAL_HOLDER.get();
        setPrincipal(principal);
        return new AuthScope(previousPrincipal);
    }

    /**
     * 清理当前线程的认证主体。线程池场景必须清理，避免污染下一个任务。
     */
    public static void clear() {
        PRINCIPAL_HOLDER.remove();
    }

    /**
     * 恢复旧认证主体，仅供 AuthScope 关闭时调用。
     */
    static void restore(AuthPrincipal principal) {
        if (principal == null) {
            clear();
            return;
        }
        PRINCIPAL_HOLDER.set(principal);
    }
}
