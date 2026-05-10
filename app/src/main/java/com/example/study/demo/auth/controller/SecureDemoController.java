package com.example.study.demo.auth.controller;

import com.example.common.core.auth.AuthContext;
import com.example.common.core.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 受保护接口示例，用来验证不带 token 返回 401，带有效 token 才能访问。
 */
@RestController
@RequestMapping("/api/secure")
public class SecureDemoController {

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong, " + AuthContext.requirePrincipal().getPrincipalName());
    }
}
