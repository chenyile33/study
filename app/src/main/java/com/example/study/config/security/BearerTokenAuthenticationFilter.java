package com.example.study.config.security;

import com.example.common.core.auth.AuthContext;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.auth.AuthScope;
import com.example.common.core.auth.TokenAuthenticator;
import com.example.common.web.auth.BearerTokenResolver;
import com.example.common.web.auth.CommonAuthProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security 版本的 Bearer token 认证过滤器。
 *
 * <p>它复用现有 TokenAuthenticator 校验 token，再把 AuthPrincipal 映射成
 * Spring Security 能识别的 Authentication。</p>
 */
@Component
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String ROLE_PREFIX = "ROLE_";

    @Resource
    private CommonAuthProperties commonAuthProperties;

    @Resource
    private BearerTokenResolver tokenResolver;

    @Resource
    private TokenAuthenticator tokenAuthenticator;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (!commonAuthProperties.isEnabled() || isPermitPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = tokenResolver.resolve(request).orElse(null);
        if (token == null) {
            // 没带 token 时交给 Spring Security 的授权环节统一触发 401。
            filterChain.doFilter(request, response);
            return;
        }

        AuthPrincipal principal = authenticate(token);
        Authentication authentication = createAuthentication(principal, token);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 当前 Controller 仍在读取 AuthContext，所以阶段二先同步写入两套上下文，方便对照学习。
        try (AuthScope ignored = AuthContext.open(principal)) {
            filterChain.doFilter(request, response);
        }
    }

    private AuthPrincipal authenticate(String token) {
        try {
            AuthPrincipal principal = tokenAuthenticator.authenticate(token);
            if (principal == null) {
                throw new BadCredentialsException("token无效");
            }
            return principal;
        } catch (AuthException exception) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException(exception.getMessage(), exception);
        }
    }

    private Authentication createAuthentication(AuthPrincipal principal, String token) {
        return new UsernamePasswordAuthenticationToken(principal, token, toAuthorities(principal));
    }

    private List<GrantedAuthority> toAuthorities(AuthPrincipal principal) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        principal.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(toRoleAuthority(role))));
        principal.getPermissions().forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        return authorities;
    }

    private String toRoleAuthority(String role) {
        return role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role;
    }

    private boolean isPermitPath(HttpServletRequest request) {
        String requestPath = normalizeRequestPath(request);
        return commonAuthProperties.getPermitPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    private String normalizeRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }
        return requestUri.isBlank() ? "/" : requestUri;
    }
}
