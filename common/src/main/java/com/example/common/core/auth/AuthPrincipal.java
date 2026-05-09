package com.example.common.core.auth;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 认证通过后的主体快照。
 *
 * <p>只保存通用身份信息，不绑定具体项目的用户表或账号模型。</p>
 */
public final class AuthPrincipal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 认证主体唯一标识，例如用户 ID、客户端 ID。
     */
    private final String principalId;

    /**
     * 认证主体展示名称，例如用户名、客户端名称。
     */
    private final String principalName;

    /**
     * 当前主体拥有的角色快照。
     */
    private final Set<String> roles;

    /**
     * 项目自定义扩展信息，例如 tenantId、clientId。
     */
    private final Map<String, String> attributes;

    private AuthPrincipal(String principalId, String principalName, Collection<String> roles,
                          Map<String, String> attributes) {
        // 构造时完成标准化，保证对象创建后就是不可变、可直接使用的主体快照。
        this.principalId = requireText(principalId);
        this.principalName = principalName == null ? "" : principalName.trim();
        this.roles = Collections.unmodifiableSet(normalizeRoles(roles));
        this.attributes = Collections.unmodifiableMap(normalizeAttributes(attributes));
    }

    public static AuthPrincipal of(String principalId, String principalName) {
        return new AuthPrincipal(principalId, principalName, Set.of(), Map.of());
    }

    /**
     * 创建带角色集合的认证主体。
     */
    public static AuthPrincipal of(String principalId, String principalName, Collection<String> roles) {
        return new AuthPrincipal(principalId, principalName, roles, Map.of());
    }

    /**
     * 创建带扩展属性的认证主体，适合携带租户、客户端等项目自定义信息。
     */
    public static AuthPrincipal of(String principalId, String principalName, Collection<String> roles,
                                   Map<String, String> attributes) {
        return new AuthPrincipal(principalId, principalName, roles, attributes);
    }

    /**
     * 判断当前主体是否拥有指定角色。
     */
    public boolean hasRole(String role) {
        return hasText(role) && roles.contains(role.trim());
    }

    /**
     * 判断当前主体是否拥有任意一个指定角色。
     */
    public boolean hasAnyRole(Collection<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return false;
        }
        return requiredRoles.stream().anyMatch(this::hasRole);
    }

    /**
     * 读取扩展属性；不存在时返回空 Optional。
     */
    public Optional<String> getAttribute(String name) {
        if (!hasText(name)) {
            return Optional.empty();
        }
        return Optional.ofNullable(attributes.get(name.trim()));
    }

    public String getPrincipalId() {
        return principalId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    private static Set<String> normalizeRoles(Collection<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return new LinkedHashSet<>();
        }

        // 去掉空角色并保持传入顺序，便于日志和调试时观察。
        Set<String> normalizedRoles = new LinkedHashSet<>();
        for (String role : roles) {
            if (hasText(role)) {
                normalizedRoles.add(role.trim());
            }
        }
        return normalizedRoles;
    }

    private static Map<String, String> normalizeAttributes(Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return new LinkedHashMap<>();
        }

        // 扩展属性只保留非空 key，value 为 null 时按空字符串保存。
        Map<String, String> normalizedAttributes = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String key = entry.getKey();
            if (hasText(key)) {
                normalizedAttributes.put(key.trim(), Objects.toString(entry.getValue(), ""));
            }
        }
        return normalizedAttributes;
    }

    private static String requireText(String value) {
        if (!hasText(value)) {
            throw new IllegalArgumentException("principalId must not be blank");
        }
        return value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
