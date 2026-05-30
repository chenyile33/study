package com.example.study.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * app 模块里两套认证学习路径的集中定义。
 */
final class SecurityPaths {

    private static final String CUSTOM_AUTH_PATH = "/api/custom-auth";
    static final String CUSTOM_AUTH_FILTER_PATTERN = "/api/custom-auth/*";

    static final String[] SECURITY_PERMIT_PATHS = {
            "/api/security/login",
            "/api/security/jwt/login",
            "/api/security/register",
            "/error"
    };

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private SecurityPaths() {
    }

    static boolean isCustomAuthRequest(HttpServletRequest request) {
        String requestPath = normalizeRequestPath(request);
        return requestPath.equals(CUSTOM_AUTH_PATH) || requestPath.startsWith(CUSTOM_AUTH_PATH + "/");
    }

    static boolean isSecurityPermitRequest(HttpServletRequest request) {
        String requestPath = normalizeRequestPath(request);
        for (String permitPath : SECURITY_PERMIT_PATHS) {
            if (PATH_MATCHER.match(permitPath, requestPath)) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }
        return requestUri.isBlank() ? "/" : requestUri;
    }
}
