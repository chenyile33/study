package com.example.common.web.auth;

import com.example.common.core.auth.AuthContext;
import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.auth.authorization.AuthorizationChecker;
import com.example.common.core.auth.authorization.RequirePermissions;
import com.example.common.core.auth.authorization.RequireRoles;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * 读取 Controller 上的授权注解，并校验当前认证主体是否满足要求。
 *
 * <p>认证过滤器 AuthFilter 先负责“你是谁”，这里再负责“你能不能访问这个接口”。</p>
 */
public class AuthorizationInterceptor implements HandlerInterceptor {

    /**
     * 认证授权总开关；关闭时本拦截器必须完全放行，不能影响接入项目。
     */
    private final CommonAuthProperties properties;

    /**
     * 具体授权判断策略；默认查 AuthPrincipal 快照，也可以由业务项目提供自定义实现。
     */
    private final AuthorizationChecker authorizationChecker;

    public AuthorizationInterceptor(CommonAuthProperties properties, AuthorizationChecker authorizationChecker) {
        this.properties = properties;
        this.authorizationChecker = authorizationChecker;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        if (!properties.isEnabled()) {
            return true;
        }

        // 静态资源、错误页等不一定是 Controller 方法，直接放行。
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 方法注解优先于类注解；类注解适合给整个 Controller 设置统一要求。
        RequireRoles requireRoles = findAuthorizationAnnotation(handlerMethod, RequireRoles.class);
        RequirePermissions requirePermissions = findAuthorizationAnnotation(handlerMethod, RequirePermissions.class);
        if (!hasRoleRestriction(requireRoles) && !hasPermissionRestriction(requirePermissions)) {
            return true;
        }

        // 能走到这里说明接口声明了角色或权限要求，因此必须已经登录。
        AuthPrincipal principal = AuthContext.requirePrincipal();
        validateRoles(principal, requireRoles);
        validatePermissions(principal, requirePermissions);
        return true;
    }

    private void validateRoles(AuthPrincipal principal, RequireRoles requireRoles) {
        if (!hasRoleRestriction(requireRoles)) {
            return;
        }

        List<String> requiredRoles = Arrays.asList(requireRoles.value());
        // 已登录但角色不满足，属于授权失败，返回 403。
        if (!authorizationChecker.hasRoles(principal, requiredRoles, requireRoles.mode())) {
            throw new AuthException(AuthErrorCode.FORBIDDEN, "角色权限不足");
        }
    }

    private void validatePermissions(AuthPrincipal principal, RequirePermissions requirePermissions) {
        if (!hasPermissionRestriction(requirePermissions)) {
            return;
        }

        List<String> requiredPermissions = Arrays.asList(requirePermissions.value());
        // 权限码更适合表达“能不能做某个操作”，例如 user:disable、blog:delete。
        if (!authorizationChecker.hasPermissions(principal, requiredPermissions, requirePermissions.mode())) {
            throw new AuthException(AuthErrorCode.FORBIDDEN, "操作权限不足");
        }
    }

    private boolean hasRoleRestriction(RequireRoles requireRoles) {
        return requireRoles != null && requireRoles.value().length > 0;
    }

    private boolean hasPermissionRestriction(RequirePermissions requirePermissions) {
        return requirePermissions != null && requirePermissions.value().length > 0;
    }

    private <A extends Annotation> A findAuthorizationAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
        // findMergedAnnotation 支持组合注解，后续如果封装 @AdminOnly 也能复用这套查找逻辑。
        A methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), annotationType);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), annotationType);
    }
}
