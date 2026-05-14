package com.example.study.demo.auth.controller;

import com.example.common.core.auth.authorization.RequirePermissions;
import com.example.common.core.page.PageParam;
import com.example.common.core.page.PageResult;
import com.example.common.core.result.Result;
import com.example.study.demo.auth.dto.AuthProfileResponse;
import com.example.study.demo.auth.service.DemoAuthProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证账号资料查询示例，复用 common 的分页模型。
 */
@RestController
@RequestMapping("/api/auth/profiles")
public class AuthProfileController {

    private final DemoAuthProfileService profileService;

    public AuthProfileController(DemoAuthProfileService profileService) {
        this.profileService = profileService;
    }

    @RequirePermissions("auth:profile:read")
    @GetMapping
    public Result<PageResult<AuthProfileResponse>> pageProfiles(
            @ModelAttribute PageParam pageParam,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(profileService.pageProfiles(pageParam, keyword));
    }
}
