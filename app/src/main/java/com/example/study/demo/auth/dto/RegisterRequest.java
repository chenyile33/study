package com.example.study.demo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    @NotBlank(message = "username不能为空")
    @Size(max = 64, message = "username长度不能超过64")
    private String username;

    @NotBlank(message = "password不能为空")
    @Size(min = 6, max = 64, message = "password长度必须在6到64之间")
    private String password;

    @NotBlank(message = "nickname不能为空")
    @Size(max = 100, message = "nickname长度不能超过100")
    private String nickname;

    @NotBlank(message = "email不能为空")
    @Email(message = "email格式错误")
    @Size(max = 100, message = "email长度不能超过100")
    private String email;
}
