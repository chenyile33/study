package com.example.study.demo.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 给账号重新分配角色的请求体。
 */
@Setter
@Getter
public class AssignAccountRolesRequest {

    @NotEmpty(message = "roleCodes不能为空")
    private List<String> roleCodes;
}
