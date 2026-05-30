package com.example.study.demo.auth.security.controller;

import com.example.common.core.page.PageParam;
import com.example.common.core.page.PageResult;
import com.example.common.core.result.Result;
import com.example.study.demo.auth.dto.AuthProfileResponse;
import com.example.study.demo.auth.service.DemoAuthProfileService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Security 版本的认证账号资料查询示例。
 *
 * <p>分页模型仍复用 common；权限声明改为 Spring Security 的 hasAuthority。</p>
 */
@RestController
@RequestMapping("/api/security/profiles")
public class SecurityAuthProfileController {

    @Resource
    private DemoAuthProfileService profileService;

    @PreAuthorize("hasAuthority('auth:profile:read')")
    @GetMapping
    public Result<PageResult<AuthProfileResponse>> pageProfiles(
            @ModelAttribute PageParam pageParam,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(profileService.pageProfiles(pageParam, keyword));
    }
}
