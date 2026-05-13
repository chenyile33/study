package com.example.study.demo.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 Demo 表实体。
 *
 * <p>这里仍然保留 UserProfile 这个名字，是为了延续 controller/dto 的学习语义。</p>
 */
@Data
@TableName("demo_user")
public class UserProfile {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("nickname")
    private String nickname;

    @TableField("email")
    private String email;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public static UserProfile create(String username, String nickname, String email) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
        userProfile.setNickname(nickname);
        userProfile.setEmail(email);
        userProfile.setCreatedAt(LocalDateTime.now());
        return userProfile;
    }

}
