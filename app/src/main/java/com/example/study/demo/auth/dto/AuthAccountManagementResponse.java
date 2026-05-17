package com.example.study.demo.auth.dto;

import com.example.study.demo.auth.entity.DemoAuthAccount;
import com.example.study.demo.auth.entity.DemoAuthPermission;
import com.example.study.demo.auth.entity.DemoAuthRole;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证管理中的账号详情，包含账号状态、角色和权限快照。
 */
@Getter
public class AuthAccountManagementResponse {

    private final Long accountId;
    private final String username;
    private final Boolean enabled;
    private final LocalDateTime createTime;
    private final LocalDateTime updateTime;
    private final List<AuthRoleResponse> roles;
    private final List<AuthPermissionResponse> permissions;

    private AuthAccountManagementResponse(Long accountId, String username, Boolean enabled,
                                          LocalDateTime createTime, LocalDateTime updateTime,
                                          List<AuthRoleResponse> roles,
                                          List<AuthPermissionResponse> permissions) {
        this.accountId = accountId;
        this.username = username;
        this.enabled = enabled;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.roles = List.copyOf(roles);
        this.permissions = List.copyOf(permissions);
    }

    public static AuthAccountManagementResponse from(DemoAuthAccount account,
                                                     List<DemoAuthRole> roles,
                                                     List<DemoAuthPermission> permissions) {
        return new AuthAccountManagementResponse(
                account.getId(),
                account.getUsername(),
                account.getEnabled(),
                account.getCreateTime(),
                account.getUpdateTime(),
                roles.stream().map(AuthRoleResponse::from).toList(),
                permissions.stream().map(AuthPermissionResponse::from).toList()
        );
    }
}
