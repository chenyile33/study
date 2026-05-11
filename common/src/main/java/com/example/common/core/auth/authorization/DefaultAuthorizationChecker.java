package com.example.common.core.auth.authorization;

import com.example.common.core.auth.AuthPrincipal;

import java.util.Collection;

/**
 * 基于 AuthPrincipal 快照的默认授权判断。
 *
 * <p>这个实现不查数据库，只适合当前学习 Demo 或 token 中已经带完整权限快照的场景。</p>
 */
public class DefaultAuthorizationChecker implements AuthorizationChecker {

    @Override
    public boolean hasRoles(AuthPrincipal principal, Collection<String> roles, AuthorizationMode mode) {
        return matches(roles, mode, principal::hasRole);
    }

    @Override
    public boolean hasPermissions(AuthPrincipal principal, Collection<String> permissions, AuthorizationMode mode) {
        return matches(permissions, mode, principal::hasPermission);
    }

    private boolean matches(Collection<String> requiredValues, AuthorizationMode mode, ValueMatcher matcher) {
        if (requiredValues == null || requiredValues.isEmpty()) {
            return true;
        }

        // mode 为空时按 ANY 处理，让注解默认语义稳定：多个值满足任意一个即可。
        AuthorizationMode actualMode = mode == null ? AuthorizationMode.ANY : mode;
        if (actualMode == AuthorizationMode.ALL) {
            return requiredValues.stream().allMatch(matcher::matches);
        }
        return requiredValues.stream().anyMatch(matcher::matches);
    }

    @FunctionalInterface
    private interface ValueMatcher {

        boolean matches(String value);
    }
}
