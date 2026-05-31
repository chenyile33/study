package com.example.common.core.auth;

/**
 * 认证作用域。
 *
 * <p>配合 try-with-resources 使用，关闭时自动恢复进入作用域前的认证主体。</p>
 *
 * <pre>
 * try (AuthScope ignored = AuthContext.open(principal)) {
 *     // 当前代码块内可以读取认证主体
 * }
 * </pre>
 */
public final class AuthScope implements AutoCloseable {

    private final AuthPrincipal previousPrincipal;
    private boolean closed;

    AuthScope(AuthPrincipal previousPrincipal) {
        this.previousPrincipal = previousPrincipal;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        // 恢复旧值，避免线程池复用时把本次认证主体留给下一次任务。
        AuthContext.restore(previousPrincipal);
        closed = true;
    }
}
