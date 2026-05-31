package com.example.study.demo.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 账号和角色的关联表实体。
 */
@Data
@TableName("demo_auth_account_role")
public class DemoAuthAccountRole {

    /**
     * 关系表真实主键是 account_id + role_id；这里标记 account_id 只是让 MyBatis-Plus 识别主键元数据。
     */
    @TableId("account_id")
    private Long accountId;

    @TableField("role_id")
    private Long roleId;
}
