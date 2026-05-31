package com.example.study.demo.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * opaque token 表实体。
 *
 * <p>token 本身没有业务含义，请求进来后必须回表查询才能恢复登录主体。</p>
 */
@Data
@TableName("demo_auth_token")
public class DemoAuthToken {

    @TableId(value = "token", type = IdType.INPUT)
    private String token;

    /**
     * token 归属账号；每次认证通过后会重新查询账号的最新角色和权限。
     */
    @TableField("account_id")
    private Long accountId;

    @TableField("expires_at")
    private LocalDateTime expiresAt;

    @TableField("create_time")
    private LocalDateTime createTime;
}
