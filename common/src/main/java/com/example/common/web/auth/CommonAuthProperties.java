package com.example.common.web.auth;

import org.springframework.core.env.Environment;

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
public final class CommonAuthProperties {

    public static final String DEFAULT_TOKEN_HEADER = "Authorization";
    public static final String DEFAULT_TOKEN_PREFIX = "Bearer";

    /**
     * 是否启用认证过滤逻辑。
     */
    private final boolean enabled;

    /**
     * 存放 token 的请求头名称。
     */
    private final String tokenHeader;

    /**
     * token 前缀，例如 Bearer。
     */
    private final String tokenPrefix;

    /**
     * 允许匿名访问的路径模式。
     */
    private final List<String> permitPaths;

    private CommonAuthProperties(boolean enabled, String tokenHeader, String tokenPrefix, List<String> permitPaths) {
        this.enabled = enabled;
        this.tokenHeader = hasText(tokenHeader) ? tokenHeader.trim() : DEFAULT_TOKEN_HEADER;
        this.tokenPrefix = tokenPrefix == null ? DEFAULT_TOKEN_PREFIX : tokenPrefix.trim();
        this.permitPaths = Collections.unmodifiableList(normalizeList(permitPaths));
    }

    public static CommonAuthProperties from(Environment environment) {
        // 认证能力已经由注解显式启用；enabled 用于运行时临时关闭过滤逻辑。
        boolean enabled = readBoolean(environment);
        String tokenHeader = readFirst(environment, DEFAULT_TOKEN_HEADER,
                "common.auth.token-header", "common.auth.tokenHeader");
        String tokenPrefix = readFirst(environment, DEFAULT_TOKEN_PREFIX,
                "common.auth.token-prefix", "common.auth.tokenPrefix");
        List<String> permitPaths = readList(environment,
                "common.auth.permit-paths", "common.auth.permitPaths");
        return new CommonAuthProperties(enabled, tokenHeader, tokenPrefix, permitPaths);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getTokenHeader() {
        return tokenHeader;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public List<String> getPermitPaths() {
        return permitPaths;
    }

    private static boolean readBoolean(Environment environment) {
        if (environment == null) {
            return true;
        }
        // 默认启用；只有显式配置 common.auth.enabled=false 时关闭。
        Boolean value = environment.getProperty("common.auth.enabled", Boolean.class);
        return value == null || value;
    }

    /**
     * 按多个配置 key 顺序读取，兼容 kebab-case 和 camelCase。
     */
    private static String readFirst(Environment environment, String defaultValue, String... keys) {
        if (environment == null) {
            return defaultValue;
        }
        for (String key : keys) {
            String value = environment.getProperty(key);
            if (hasText(value)) {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * 读取列表配置，支持 common.auth.permit-paths=a,b 和 permit-paths[0] 两种形式。
     */
    private static List<String> readList(Environment environment, String... keys) {
        List<String> values = new ArrayList<>();
        if (environment == null) {
            return values;
        }

        // 同时兼容逗号分隔和 YAML 列表两种写法。
        for (String key : keys) {
            addDelimited(values, environment.getProperty(key));
            for (int i = 0; ; i++) {
                String item = environment.getProperty(key + "[" + i + "]");
                if (item == null) {
                    break;
                }
                addDelimited(values, item);
            }
        }
        return values;
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

    private static void addDelimited(List<String> values, String rawValue) {
        if (!hasText(rawValue)) {
            return;
        }
        String[] items = rawValue.split(",");
        for (String item : items) {
            if (hasText(item)) {
                values.add(item.trim());
            }
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
