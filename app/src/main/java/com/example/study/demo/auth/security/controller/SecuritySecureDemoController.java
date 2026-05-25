package com.example.study.demo.auth.security.controller;

import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.result.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Security 版本的受保护接口示例。
 *
 * <p>这里不再直接读取 AuthContext，也不使用 common 自研授权注解；
 * 当前登录主体来自 Spring Security 的 SecurityContextHolder。</p>
 */
@RestController
@RequestMapping("/api/security/secure")
public class SecuritySecureDemoController {

    @GetMapping("/ping")
    public Result<String> ping(@AuthenticationPrincipal AuthPrincipal principal) {
        return Result.success("pong, " + principal.getPrincipalName());
    }

    /**
     * Spring Security 角色控制示例：hasRole('ADMIN') 会匹配 ROLE_ADMIN authority。
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public Result<String> adminOnly() {
        return Result.success("Spring Security hasRole ADMIN 通过");
    }

    /**
     * Spring Security 权限码控制示例：权限码直接按 authority 字符串匹配。
     */
    @PreAuthorize("hasAuthority('secure:read')")
    @GetMapping("/permission")
    public Result<String> permissionOnly() {
        return Result.success("Spring Security hasAuthority secure:read 通过");
    }
}
