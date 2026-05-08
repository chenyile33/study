package com.example.study.demo.user.domain;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 演示用用户领域对象，先保持纯内存模型。
 */
@Getter
public class UserProfile {

    private final Long id;
    private final String username;
    private final String nickname;
    private final String email;
    private final LocalDateTime createdAt;

    private UserProfile(Long id, String username, String nickname, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.createdAt = createdAt;
    }

    public static UserProfile create(Long id, String username, String nickname, String email) {
        return new UserProfile(id, username, nickname, email, LocalDateTime.now());
    }

}
