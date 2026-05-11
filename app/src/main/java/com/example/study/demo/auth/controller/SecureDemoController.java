package com.example.study.demo.auth.controller;

import com.example.common.core.auth.AuthContext;
import com.example.common.core.auth.authorization.RequirePermissions;
import com.example.common.core.auth.authorization.RequireRoles;
import com.example.common.core.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 受保护接口示例，用来验证不带 token 返回 401，带有效 token 才能访问。
 */
@RestController
@RequestMapping("/api/secure")
public class SecureDemoController {

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong, " + AuthContext.requirePrincipal().getPrincipalName());
    }

    /**
     * 角色控制示例：alice 只有 USER 角色，访问这里会返回 403。
     */
    @RequireRoles("ADMIN")
    @GetMapping("/admin")
    public Result<String> adminOnly() {
        return Result.success("只有 ADMIN 角色可以访问");
    }

    /**
     * 权限码控制示例：admin 和 alice 都有 secure:read，所以都能访问。
     */
    @RequirePermissions("secure:read")
    @GetMapping("/permission")
    public Result<String> permissionOnly() {
        return Result.success("拥有 secure:read 权限码即可访问");
    }
}
