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
 * <p>当前 Demo 为了聚焦认证流程，密码仍是明文；真实项目应改为加盐哈希。</p>
 */
@Data
@TableName("demo_auth_account")
public class DemoAuthAccount {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    /**
     * 学习 Demo 中直接保存明文密码，方便观察登录校验链路。
     */
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
