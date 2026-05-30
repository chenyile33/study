package com.example.study.demo.auth.security.controller;

import com.example.common.core.result.Result;
import com.example.study.demo.auth.dto.AssignAccountRolesRequest;
import com.example.study.demo.auth.dto.AssignRolePermissionsRequest;
import com.example.study.demo.auth.dto.AuthAccountManagementResponse;
import com.example.study.demo.auth.dto.AuthPermissionResponse;
import com.example.study.demo.auth.dto.AuthRoleResponse;
import com.example.study.demo.auth.dto.UpdateAccountEnabledRequest;
import com.example.study.demo.auth.service.DemoAuthManagementService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Spring Security 版本的认证管理 Demo。
 *
 * <p>这里使用 @PreAuthorize 和 hasAuthority(...) 声明权限要求，
 * 用来对照 custom 包里的 @RequirePermissions 写法。</p>
 */
@RestController
@RequestMapping("/api/security/management")
public class SecurityAuthManagementController {

    @Resource
    private DemoAuthManagementService managementService;

    @PreAuthorize("hasAuthority('auth:account:read')")
    @GetMapping("/accounts/{accountId}")
    public Result<AuthAccountManagementResponse> getAccount(@PathVariable Long accountId) {
        return Result.success(managementService.getAccount(accountId));
    }

    @PreAuthorize("hasAuthority('auth:account:write')")
    @PatchMapping("/accounts/{accountId}/enabled")
    public Result<AuthAccountManagementResponse> updateAccountEnabled(
            @PathVariable Long accountId,
            @Valid @RequestBody(required = false) UpdateAccountEnabledRequest request
    ) {
        return Result.success(managementService.updateAccountEnabled(accountId, request));
    }

    @PreAuthorize("hasAuthority('auth:role:write')")
    @PutMapping("/accounts/{accountId}/roles")
    public Result<AuthAccountManagementResponse> assignAccountRoles(
            @PathVariable Long accountId,
            @Valid @RequestBody(required = false) AssignAccountRolesRequest request
    ) {
        return Result.success(managementService.assignAccountRoles(accountId, request));
    }

    @PreAuthorize("hasAuthority('auth:role:read')")
    @GetMapping("/roles")
    public Result<List<AuthRoleResponse>> listRoles() {
        return Result.success(managementService.listRoles());
    }

    @PreAuthorize("hasAuthority('auth:permission:read')")
    @GetMapping("/permissions")
    public Result<List<AuthPermissionResponse>> listPermissions() {
        return Result.success(managementService.listPermissions());
    }

    @PreAuthorize("hasAuthority('auth:permission:write')")
    @PutMapping("/roles/{roleCode}/permissions")
    public Result<List<AuthPermissionResponse>> assignRolePermissions(
            @PathVariable String roleCode,
            @Valid @RequestBody(required = false) AssignRolePermissionsRequest request
    ) {
        return Result.success(managementService.assignRolePermissions(roleCode, request));
    }
}
