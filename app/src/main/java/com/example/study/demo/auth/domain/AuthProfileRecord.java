package com.example.study.demo.auth.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认证账号和资料表联查后的分页记录。
 */
@Data
public class AuthProfileRecord {

    private Long accountId;

    private String username;

    private String nickname;

    private String email;

    private Boolean enabled;

    private LocalDateTime createTime;
}
