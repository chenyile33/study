package com.example.study.demo.auth.custom.controller;

import com.example.common.core.auth.authorization.RequirePermissions;
import com.example.common.core.page.PageParam;
import com.example.common.core.page.PageResult;
import com.example.common.core.result.Result;
import com.example.study.demo.auth.dto.AuthProfileResponse;
import com.example.study.demo.auth.service.DemoAuthProfileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * common 自研权限注解版本的认证账号资料查询示例。
 *
 * <p>这个类同时复用 common 的分页模型和 @RequirePermissions 权限声明。</p>
 */
@RestController
@RequestMapping("/api/custom-auth/profiles")
public class CustomAuthProfileController {

    @Resource
    private DemoAuthProfileService profileService;

    @RequirePermissions("auth:profile:read")
    @GetMapping
    public Result<PageResult<AuthProfileResponse>> pageProfiles(
            @ModelAttribute PageParam pageParam,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(profileService.pageProfiles(pageParam, keyword));
    }
}
