package com.example.study.user.controller;

import com.example.common.core.page.PageParam;
import com.example.common.core.page.PageResult;
import com.example.common.core.result.Result;
import com.example.study.user.dto.CreateUserRequest;
import com.example.study.user.dto.UserDetailResponse;
import com.example.study.user.dto.UserListItemResponse;
import com.example.study.user.service.UserDemoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserDemoController {

    private static final Logger log = LoggerFactory.getLogger(UserDemoController.class);

    private final UserDemoService userDemoService;

    public UserDemoController(UserDemoService userDemoService) {
        this.userDemoService = userDemoService;
    }

    @PostMapping
    public Result<UserDetailResponse> createUser(@Valid @RequestBody(required = false) CreateUserRequest request) {
        log.info("创建用户，username={}", request == null ? null : request.getUsername());
        return Result.success(userDemoService.createUser(request));
    }

    @GetMapping("/{id}")
    public Result<UserDetailResponse> getUser(@PathVariable Long id) {
        log.info("查询用户详情，id={}", id);
        return Result.success(userDemoService.getUser(id));
    }

    @GetMapping
    public Result<PageResult<UserListItemResponse>> pageUsers(
            @ModelAttribute PageParam pageParam,
            @RequestParam(required = false) String keyword
    ) {
        log.info("分页查询用户，pageNum={}, pageSize={}, keyword={}",
                pageParam.getPageNum(), pageParam.getPageSize(), keyword);
        return Result.success(userDemoService.pageUsers(pageParam, keyword));
    }
}
