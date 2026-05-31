package com.example.common.web.auth;

import com.example.common.core.auth.AuthContext;
import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.auth.AuthScope;
import com.example.common.core.auth.TokenAuthenticator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 根据请求 token 恢复认证上下文。
 *
 * <p>过滤器只负责 Web 层接入；token 的真实校验逻辑由接入项目的 TokenAuthenticator 提供。</p>
 */
public class AuthFilter extends OncePerRequestFilter {

    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

    /**
     * 认证开关、token 头、白名单等配置。
     */
    private final CommonAuthProperties properties;

    /**
     * 负责从请求中提取 token 字符串。
     */
    private final BearerTokenResolver tokenResolver;

    /**
     * 负责把 token 校验成认证主体。
     */
    private final TokenAuthenticator tokenAuthenticator;

    /**
     * 用于匹配 permit-paths 中的通配路径。
     */
    private final PathMatcher pathMatcher = new AntPathMatcher();

    public AuthFilter(CommonAuthProperties properties, BearerTokenResolver tokenResolver,
                      TokenAuthenticator tokenAuthenticator) {
        this.properties = properties;
        this.tokenResolver = tokenResolver;
        this.tokenAuthenticator = tokenAuthenticator;
    }

    /**
     * 请求进入 Controller 前执行认证。
     *
     * <p>匿名路径直接放行；受保护路径必须解析并校验 token。</p>
     */
    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (!properties.isEnabled() || isPermitPath(request)) {
            // 认证关闭或白名单请求不写入主体，并清理进入过滤器前可能残留的上下文。
            try (AuthScope ignored = AuthContext.openAnonymous()) {
                filterChain.doFilter(request, response);
            }
            return;
        }

        // 受保护请求必须提供 token。
        String token = tokenResolver.resolve(request).orElse(null);
        if (token == null) {
            writeAuthError(response, AuthErrorCode.UNAUTHORIZED.getCode(), "未登录");
            return;
        }

        AuthPrincipal principal;
        try {
            // 具体校验逻辑留给接入项目，实现可以选择 opaque token、JWT 或其他方案。
            principal = tokenAuthenticator.authenticate(token);
        } catch (AuthException exception) {
            writeAuthError(response, exception.getCode(), exception.getMessage());
            return;
        }

        if (principal == null) {
            writeAuthError(response, AuthErrorCode.UNAUTHORIZED.getCode(), "token无效");
            return;
        }

        // 认证通过后在本次请求作用域内写入主体，请求结束自动恢复。
        try (AuthScope ignored = AuthContext.open(principal)) {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 判断当前请求是否允许匿名访问。
     */
    private boolean isPermitPath(HttpServletRequest request) {
        String requestPath = normalizeRequestPath(request);
        return properties.getPermitPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    private String normalizeRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        // 去掉 context-path，让 permit-paths 只关注应用内路径。
        if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }
        return requestUri.isBlank() ? "/" : requestUri;
    }

    private void writeAuthError(HttpServletResponse response, int code, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        // 过滤器发生在 Controller 前面，这里直接写 Result 结构的 JSON 响应。
        int status = AuthErrorCode.FORBIDDEN.getCode() == code
                ? HttpServletResponse.SC_FORBIDDEN
                : HttpServletResponse.SC_UNAUTHORIZED;
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(JSON_CONTENT_TYPE);
        response.getWriter().write(toJson(code, message));
    }

    private String toJson(int code, String message) {
        return "{\"code\":" + code + ",\"message\":\"" + escapeJson(message)
                + "\",\"data\":null,\"success\":false}";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escapedValue = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            switch (current) {
                case '\\' -> escapedValue.append("\\\\");
                case '"' -> escapedValue.append("\\\"");
                case '\b' -> escapedValue.append("\\b");
                case '\f' -> escapedValue.append("\\f");
                case '\n' -> escapedValue.append("\\n");
                case '\r' -> escapedValue.append("\\r");
                case '\t' -> escapedValue.append("\\t");
                default -> {
                    if (current < 0x20) {
                        escapedValue.append(String.format("\\u%04x", (int) current));
                    } else {
                        escapedValue.append(current);
                    }
                }
            }
        }
        return escapedValue.toString();
    }
}
