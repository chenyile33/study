package com.example.study.config.security;

import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.auth.TokenAuthenticator;
import com.example.common.web.auth.BearerTokenResolver;
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
    private BearerTokenResolver tokenResolver;

    @Resource
    private TokenAuthenticator tokenAuthenticator;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (isPermitPath(request)) {
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
        Authentication authentication = createAuthentication(principal);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
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

    private Authentication createAuthentication(AuthPrincipal principal) {
        // token 只用于本次校验，不再放进 Authentication，避免后续日志或调试时误暴露凭证。
        return new UsernamePasswordAuthenticationToken(principal, null, toAuthorities(principal));
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
        return SecurityPaths.isSecurityPermitRequest(request);
    }
}
