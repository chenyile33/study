package com.example.common.web.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * common-web 认证配置。
 *
 * <p>由 @EnableCommonAuthWeb 引入后读取 common.auth.* 配置。</p>
 */
@ConfigurationProperties(prefix = "common.auth")
public class CommonAuthProperties {

    public static final String DEFAULT_TOKEN_HEADER = "Authorization";
    public static final String DEFAULT_TOKEN_PREFIX = "Bearer";

    /**
     * 是否启用认证过滤逻辑。
     */
    private boolean enabled = true;

    /**
     * 存放 token 的请求头名称。
     */
    private String tokenHeader = DEFAULT_TOKEN_HEADER;

    /**
     * token 前缀，例如 Bearer。
     */
    private String tokenPrefix = DEFAULT_TOKEN_PREFIX;

    /**
     * 允许匿名访问的路径模式。
     */
    private List<String> permitPaths = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTokenHeader() {
        return tokenHeader;
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = hasText(tokenHeader) ? tokenHeader.trim() : DEFAULT_TOKEN_HEADER;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix == null ? DEFAULT_TOKEN_PREFIX : tokenPrefix.trim();
    }

    public List<String> getPermitPaths() {
        return Collections.unmodifiableList(permitPaths);
    }

    /**
     * Spring Boot 会把 common.auth.permit-paths 绑定到这里。
     */
    public void setPermitPaths(List<String> permitPaths) {
        this.permitPaths = normalizeList(permitPaths);
    }

    /**
     * 清理空路径、去重，并把路径统一成以 / 开头的形式。
     */
    private static List<String> normalizeList(List<String> values) {
        Set<String> normalizedValues = new LinkedHashSet<>();
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        for (String value : values) {
            if (hasText(value)) {
                normalizedValues.add(normalizePath(value));
            }
        }
        return new ArrayList<>(normalizedValues);
    }

    private static String normalizePath(String value) {
        String path = value.trim();
        // permit-paths 可以写成 api/login，内部统一成 /api/login。
        return path.startsWith("/") ? path : "/" + path;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
