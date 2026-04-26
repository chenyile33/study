package com.example.study.web.controller;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.common.core.result.Result;
import com.example.common.core.util.AssertUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于验证 common-core 统一返回和业务异常链路。
 */
@RestController
@RequestMapping("/api/common-core")
public class CommonCoreDemoController {

    @GetMapping("/success")
    public Result<String> success(@RequestParam(defaultValue = "study") String name) {
        return Result.success("hello, " + name);
    }

    @GetMapping("/assert")
    public Result<String> assertName(@RequestParam(required = false) String name) {
        AssertUtils.hasText(name, CommonErrorCode.PARAM_ERROR, "name不能为空");
        return Result.success("hello, " + name.trim());
    }

    @GetMapping("/business-error")
    public Result<Void> businessError() {
        throw new BusinessException(CommonErrorCode.PARAM_ERROR, "演示业务异常");
    }
}
