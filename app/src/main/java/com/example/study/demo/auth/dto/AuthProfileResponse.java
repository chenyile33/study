package com.example.study.demo.auth.dto;

import com.example.study.demo.auth.entity.DemoAuthAccount;
import com.example.study.demo.auth.entity.DemoAuthProfile;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 认证账号资料分页返回项。
 */
@Getter
public class AuthProfileResponse {

    private final Long accountId;
    private final String username;
    private final String nickname;
    private final String email;
    private final Boolean enabled;
    private final LocalDateTime createTime;

    private AuthProfileResponse(Long accountId, String username, String nickname, String email,
                                Boolean enabled, LocalDateTime createTime) {
        this.accountId = accountId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.enabled = enabled;
        this.createTime = createTime;
    }

    public static AuthProfileResponse from(DemoAuthAccount account, DemoAuthProfile profile) {
        return new AuthProfileResponse(
                account.getId(),
                account.getUsername(),
                profile.getNickname(),
                profile.getEmail(),
                account.getEnabled(),
                profile.getCreateTime()
        );
    }
}
