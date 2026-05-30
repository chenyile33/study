package com.example.study.demo.auth.security.controller;

import com.example.common.core.auth.AuthPrincipal;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Security 版本的认证入口示例。
 *
 * <p>登录和注册仍复用 app 的业务服务；区别在于受保护接口通过
 * @AuthenticationPrincipal 从 SecurityContextHolder 读取当前主体。</p>
 */
@RestController
@RequestMapping("/api/security")
public class SecurityAuthDemoController {

    @Resource
    private DemoTokenService tokenService;

    @Resource
    private DemoRegistrationService registrationService;

    @Resource
    private BearerTokenResolver tokenResolver;

    /**
     * Spring Security 版本的注册接口，在 SecurityPaths 的白名单中允许匿名访问。
     */
    @PostMapping("/register")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(registrationService.register(request));
    }

    /**
     * opaque token 登录：登录成功后仍由后续 BearerTokenAuthenticationFilter 校验 token。
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(tokenService.login(request));
    }

    /**
     * JWT 登录：token 自身携带主体和权限快照，过滤器校验后写入 SecurityContextHolder。
     */
    @PostMapping("/jwt/login")
    public Result<LoginResponse> jwtLogin(@Valid @RequestBody LoginRequest request) {
        return Result.success(tokenService.loginWithJwt(request));
    }

    /**
     * Spring Security 写法：当前主体来自 @AuthenticationPrincipal，不再直接读取 AuthContext。
     */
    @GetMapping("/me")
    public Result<AuthPrincipalResponse> me(@AuthenticationPrincipal AuthPrincipal principal) {
        return Result.success(AuthPrincipalResponse.from(principal));
    }

    /**
     * logout 仍然只对 opaque token 有主动失效效果；JWT 需要黑名单机制才能主动失效。
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        tokenResolver.resolve(request).ifPresent(tokenService::logout);
        return Result.success();
    }
}
