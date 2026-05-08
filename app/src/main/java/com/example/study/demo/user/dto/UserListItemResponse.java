package com.example.study.demo.user.dto;

import com.example.study.demo.user.domain.UserProfile;

public class UserListItemResponse {

    private final Long id;
    private final String username;
    private final String nickname;

    private UserListItemResponse(Long id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }

    public static UserListItemResponse from(UserProfile userProfile) {
        return new UserListItemResponse(
                userProfile.getId(),
                userProfile.getUsername(),
                userProfile.getNickname()
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
}
