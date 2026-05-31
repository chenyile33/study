package com.example.study.demo.auth.dto;

import com.example.study.demo.auth.entity.DemoAuthPermission;
import lombok.Getter;

/**
 * 权限码返回项。
 */
@Getter
public class AuthPermissionResponse {

    private final Long id;
    private final String permissionCode;
    private final String permissionName;

    private AuthPermissionResponse(Long id, String permissionCode, String permissionName) {
        this.id = id;
        this.permissionCode = permissionCode;
        this.permissionName = permissionName;
    }

    public static AuthPermissionResponse from(DemoAuthPermission permission) {
        return new AuthPermissionResponse(
                permission.getId(),
                permission.getPermissionCode(),
                permission.getPermissionName()
        );
    }
}
