package com.example.study.demo.auth.controller;

import com.example.common.core.auth.AuthContext;
import com.example.common.core.result.Result;
import com.example.common.web.auth.BearerTokenResolver;
import com.example.study.demo.auth.dto.AuthPrincipalResponse;
import com.example.study.demo.auth.dto.LoginRequest;
import com.example.study.demo.auth.dto.LoginResponse;
import com.example.study.demo.auth.dto.RegisterRequest;
import com.example.study.demo.auth.dto.RegisterResponse;
import com.example.study.demo.auth.service.DemoRegistrationService;
import com.example.study.demo.auth.service.DemoTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * app 层认证 Demo，演示具体项目如何接入 common 认证抽象。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthDemoController {

    private final DemoTokenService tokenService;
    private final DemoRegistrationService registrationService;
    private final BearerTokenResolver tokenResolver;

    public AuthDemoController(DemoTokenService tokenService,
                              DemoRegistrationService registrationService,
                              BearerTokenResolver tokenResolver) {
        this.tokenService = tokenService;
        this.registrationService = registrationService;
        this.tokenResolver = tokenResolver;
    }

    /**
     * 注册接口在 permit-paths 中，允许匿名创建学习账号。
     */
    @PostMapping("/register")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(registrationService.register(request));
    }

    /**
     * 登录接口在 permit-paths 中，允许匿名访问。
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(tokenService.login(request));
    }

    /**
     * JWT 登录接口同样使用 Bearer 头传回，但 token 本身携带主体和权限快照。
     */
    @PostMapping("/jwt/login")
    public Result<LoginResponse> jwtLogin(@Valid @RequestBody LoginRequest request) {
        return Result.success(tokenService.loginWithJwt(request));
    }

    @GetMapping("/me")
    public Result<AuthPrincipalResponse> me() {
        return Result.success(AuthPrincipalResponse.from(AuthContext.requirePrincipal()));
    }

    /**
     * logout 会删除 opaque token 的服务端状态；JWT 本身无状态，需要黑名单机制才能主动失效。
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        tokenResolver.resolve(request).ifPresent(tokenService::logout);
        return Result.success();
    }
}
