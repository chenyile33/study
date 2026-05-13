package com.example.study.demo.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色表实体，角色表达一组身份或职责。
 */
@Data
@TableName("demo_auth_role")
public class DemoAuthRole {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 写入 AuthPrincipal 后会被 @RequireRoles 使用。
     */
    @TableField("role_code")
    private String roleCode;

    @TableField("role_name")
    private String roleName;
}
