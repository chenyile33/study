package com.example.study.demo.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 给角色重新分配权限码的请求体。
 */
@Setter
@Getter
public class AssignRolePermissionsRequest {

    @NotEmpty(message = "permissionCodes不能为空")
    private List<String> permissionCodes;
}
