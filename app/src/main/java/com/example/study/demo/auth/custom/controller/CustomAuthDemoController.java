package com.example.study.demo.auth.custom.controller;

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
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 只使用 common 自研认证能力的认证入口示例。
 *
 * <p>这里故意通过 AuthContext 读取当前主体，方便和 Spring Security 的
 * @AuthenticationPrincipal 写法做对照。</p>
 */
@RestController
@RequestMapping("/api/custom-auth")
public class CustomAuthDemoController {

    @Resource
    private DemoTokenService tokenService;

    @Resource
    private DemoRegistrationService registrationService;

    @Resource
    private BearerTokenResolver tokenResolver;

    /**
     * 注册接口在 permit-paths 中，允许匿名创建学习账号。
     */
    @PostMapping("/register")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(registrationService.register(request));
    }

    /**
     * 登录接口在 permit-paths 中，允许匿名访问；成功后签发 opaque token。
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(tokenService.login(request));
    }

    /**
     * JWT 登录接口同样使用 Bearer 头传回，但 token 本身携带主体和权限快照。
     * 这里仍复用 common 的 TokenAuthenticator 入口校验。
     */
    @PostMapping("/jwt/login")
    public Result<LoginResponse> jwtLogin(@Valid @RequestBody LoginRequest request) {
        return Result.success(tokenService.loginWithJwt(request));
    }

    @GetMapping("/me")
    public Result<AuthPrincipalResponse> me() {
        // common 自研认证写法：业务代码从 AuthContext 取当前登录主体。
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
