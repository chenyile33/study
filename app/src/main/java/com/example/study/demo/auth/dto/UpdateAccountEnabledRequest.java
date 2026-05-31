package com.example.study.demo.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改账号启用状态的请求体。
 */
@Setter
@Getter
public class UpdateAccountEnabledRequest {

    @NotNull(message = "enabled不能为空")
    private Boolean enabled;
}
