package com.example.common.core.auth.authorization;

/**
 * 多个角色或权限的匹配方式。
 *
 * <p>例如 @RequirePermissions(value = {"a", "b"}, mode = ALL) 表示 a 和 b 都必须拥有。</p>
 */
public enum AuthorizationMode {

    /**
     * 满足任意一个即可通过。
     */
    ANY,

    /**
     * 必须全部满足才通过。
     */
    ALL
}
