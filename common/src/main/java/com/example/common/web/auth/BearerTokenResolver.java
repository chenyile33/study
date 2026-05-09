package com.example.common.web.auth;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * 从 HTTP 请求中解析访问 token。
 *
 * <p>默认读取 Authorization: Bearer token，也可以通过配置改 header 或 prefix。</p>
 */
public class BearerTokenResolver {

    /**
     * token 请求头和前缀配置。
     */
    private final CommonAuthProperties properties;

    public BearerTokenResolver(CommonAuthProperties properties) {
        this.properties = properties;
    }

    public Optional<String> resolve(HttpServletRequest request) {
        if (request == null) {
            return Optional.empty();
        }

        // header 不存在时交给认证过滤器决定是否返回未登录。
        String headerValue = request.getHeader(properties.getTokenHeader());
        if (!hasText(headerValue)) {
            return Optional.empty();
        }

        String value = headerValue.trim();
        String prefix = properties.getTokenPrefix();
        if (!hasText(prefix)) {
            // prefix 配为空时，整个 header 值都视为 token。
            return Optional.of(value);
        }

        // Bearer 前缀大小写不敏感，但前缀和 token 之间必须有空白字符。
        String normalizedPrefix = prefix.trim();
        if (!value.regionMatches(true, 0, normalizedPrefix, 0, normalizedPrefix.length())) {
            return Optional.empty();
        }
        if (value.length() == normalizedPrefix.length()
                || !Character.isWhitespace(value.charAt(normalizedPrefix.length()))) {
            return Optional.empty();
        }

        String token = value.substring(normalizedPrefix.length()).trim();
        return hasText(token) ? Optional.of(token) : Optional.empty();
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
