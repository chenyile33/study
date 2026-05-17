package com.example.study.demo.auth.dto;

import com.example.study.demo.auth.entity.DemoAuthRole;
import lombok.Getter;

/**
 * 角色返回项。
 */
@Getter
public class AuthRoleResponse {

    private final Long id;
    private final String roleCode;
    private final String roleName;

    private AuthRoleResponse(Long id, String roleCode, String roleName) {
        this.id = id;
        this.roleCode = roleCode;
        this.roleName = roleName;
    }

    public static AuthRoleResponse from(DemoAuthRole role) {
        return new AuthRoleResponse(role.getId(), role.getRoleCode(), role.getRoleName());
    }
}
