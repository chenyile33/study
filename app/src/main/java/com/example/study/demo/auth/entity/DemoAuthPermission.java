package com.example.study.demo.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限码表实体，权限码表达“能做什么操作”，例如 blog:delete。
 */
@Data
@TableName("demo_auth_permission")
public class DemoAuthPermission {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 写入 AuthPrincipal 后会被 @RequirePermissions 使用。
     */
    @TableField("permission_code")
    private String permissionCode;

    @TableField("permission_name")
    private String permissionName;
}
