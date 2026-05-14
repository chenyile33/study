package com.example.study.demo.auth.service;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.common.core.util.AssertUtils;
import com.example.study.demo.auth.dto.RegisterRequest;
import com.example.study.demo.auth.dto.RegisterResponse;
import com.example.study.demo.auth.password.PasswordHasher;
import com.example.study.demo.auth.repository.DemoRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注册认证账号，并为账号建立默认资料和 USER 角色。
 */
@Service
public class DemoRegistrationService {

    private final DemoRegistrationRepository registrationRepository;
    private final PasswordHasher passwordHasher;

    public DemoRegistrationService(DemoRegistrationRepository registrationRepository, PasswordHasher passwordHasher) {
        this.registrationRepository = registrationRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        validateRegisterRequest(request);
        String username = request.getUsername().trim();
        AssertUtils.isTrue(
                !registrationRepository.existsByUsername(username),
                CommonErrorCode.PARAM_ERROR,
                "username已存在"
        );

        DemoRegistrationRepository.RegisteredAccount registeredAccount = registrationRepository.saveUserAccount(
                username,
                passwordHasher.hash(request.getPassword()),
                request.getNickname().trim(),
                request.getEmail().trim()
        );
        return RegisterResponse.from(registeredAccount.account(), registeredAccount.profile());
    }

    private static void validateRegisterRequest(RegisterRequest request) {
        AssertUtils.notNull(request, CommonErrorCode.PARAM_ERROR, "请求体不能为空");
        AssertUtils.hasText(request.getUsername(), CommonErrorCode.PARAM_ERROR, "username不能为空");
        AssertUtils.hasText(request.getPassword(), CommonErrorCode.PARAM_ERROR, "password不能为空");
        AssertUtils.hasText(request.getNickname(), CommonErrorCode.PARAM_ERROR, "nickname不能为空");
        AssertUtils.hasText(request.getEmail(), CommonErrorCode.PARAM_ERROR, "email不能为空");
        if (request.getPassword().length() < 6 || request.getPassword().length() > 64) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "password长度必须在6到64之间");
        }
    }
}
