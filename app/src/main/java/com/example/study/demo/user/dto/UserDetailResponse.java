package com.example.study.demo.user.dto;

import com.example.study.demo.user.entity.UserProfile;

import java.time.LocalDateTime;

public class UserDetailResponse {

    private final Long id;
    private final String username;
    private final String nickname;
    private final String email;
    private final LocalDateTime createdAt;

    private UserDetailResponse(Long id, String username, String nickname, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.createdAt = createdAt;
    }

    public static UserDetailResponse from(UserProfile userProfile) {
        return new UserDetailResponse(
                userProfile.getId(),
                userProfile.getUsername(),
                userProfile.getNickname(),
                userProfile.getEmail(),
                userProfile.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
