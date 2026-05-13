package com.example.study.demo.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色和权限码的关联表实体。
 */
@Data
@TableName("demo_auth_role_permission")
public class DemoAuthRolePermission {

    /**
     * 关系表真实主键是 role_id + permission_id；这里标记 role_id 只是让 MyBatis-Plus 识别主键元数据。
     */
    @TableId("role_id")
    private Long roleId;

    @TableField("permission_id")
    private Long permissionId;
}
