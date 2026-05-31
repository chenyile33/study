package com.example.study.demo.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认证账号的资料表实体。
 *
 * <p>登录凭证放在 demo_auth_account，昵称、邮箱等展示资料放在这里。</p>
 */
@Data
@TableName("demo_auth_profile")
public class DemoAuthProfile {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("account_id")
    private Long accountId;

    @TableField("nickname")
    private String nickname;

    @TableField("email")
    private String email;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public static DemoAuthProfile create(Long accountId, String nickname, String email) {
        DemoAuthProfile profile = new DemoAuthProfile();
        profile.setAccountId(accountId);
        profile.setNickname(nickname);
        profile.setEmail(email);
        profile.setCreateTime(LocalDateTime.now());
        profile.setUpdateTime(LocalDateTime.now());
        return profile;
    }
}
