package com.example.study.demo.auth.dto;

import com.example.study.demo.auth.entity.DemoAuthAccount;
import com.example.study.demo.auth.entity.DemoAuthProfile;
import lombok.Getter;

/**
 * 注册成功后返回的账号和资料摘要。
 */
@Getter
public class RegisterResponse {

    private final Long accountId;
    private final String username;
    private final String nickname;
    private final String email;

    private RegisterResponse(Long accountId, String username, String nickname, String email) {
        this.accountId = accountId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
    }

    public static RegisterResponse from(DemoAuthAccount account, DemoAuthProfile profile) {
        return new RegisterResponse(
                account.getId(),
                account.getUsername(),
                profile.getNickname(),
                profile.getEmail()
        );
    }
}
