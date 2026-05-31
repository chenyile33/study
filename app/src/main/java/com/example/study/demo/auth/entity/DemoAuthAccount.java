package com.example.study.demo.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认证账号表实体。
 *
 * <p>password 字段保存 BCrypt 哈希值，不保存明文密码。</p>
 */
@Data
@TableName("demo_auth_account")
public class DemoAuthAccount {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    /**
     * false 表示账号被停用，登录和 token 恢复都应该失败。
     */
    @TableField("enabled")
    private Boolean enabled;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
