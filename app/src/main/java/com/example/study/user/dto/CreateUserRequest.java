package com.example.study.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "username不能为空")
    @Size(max = 50, message = "username长度不能超过50")
    private String username;

    @NotBlank(message = "nickname不能为空")
    @Size(max = 50, message = "nickname长度不能超过50")
    private String nickname;

    @NotBlank(message = "email不能为空")
    @Email(message = "email格式错误")
    @Size(max = 100, message = "email长度不能超过100")
    private String email;

}
