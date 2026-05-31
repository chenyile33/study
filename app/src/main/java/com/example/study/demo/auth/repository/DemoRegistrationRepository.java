package com.example.study.demo.auth.repository;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.study.demo.auth.entity.DemoAuthAccount;
import com.example.study.demo.auth.entity.DemoAuthAccountRole;
import com.example.study.demo.auth.entity.DemoAuthProfile;
import com.example.study.demo.auth.entity.DemoAuthRole;
import com.example.study.demo.auth.mapper.DemoAuthAccountMapper;
import com.example.study.demo.auth.mapper.DemoAuthAccountRoleMapper;
import com.example.study.demo.auth.mapper.DemoAuthProfileMapper;
import com.example.study.demo.auth.mapper.DemoAuthRoleMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 注册流程的数据库写入仓库。
 */
@Repository
public class DemoRegistrationRepository {

    private static final String DEFAULT_ROLE_CODE = "USER";

    private final DemoAuthAccountMapper accountMapper;
    private final DemoAuthRoleMapper roleMapper;
    private final DemoAuthAccountRoleMapper accountRoleMapper;
    private final DemoAuthProfileMapper profileMapper;

    public DemoRegistrationRepository(DemoAuthAccountMapper accountMapper,
                                      DemoAuthRoleMapper roleMapper,
                                      DemoAuthAccountRoleMapper accountRoleMapper,
                                      DemoAuthProfileMapper profileMapper) {
        this.accountMapper = accountMapper;
        this.roleMapper = roleMapper;
        this.accountRoleMapper = accountRoleMapper;
        this.profileMapper = profileMapper;
    }

    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return accountMapper.countByUsername(username.trim()) > 0;
    }

    public RegisteredAccount saveUserAccount(String username, String passwordHash, String nickname, String email) {
        DemoAuthAccount account = new DemoAuthAccount();
        account.setUsername(username);
        account.setPassword(passwordHash);
        account.setEnabled(true);
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.insert(account);

        // 默认角色由初始化 SQL 提供；查询写在 XML，注册流程只关心业务含义。
        DemoAuthRole userRole = roleMapper.selectByRoleCode(DEFAULT_ROLE_CODE);
        if (userRole == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "默认USER角色不存在");
        }

        DemoAuthAccountRole accountRole = new DemoAuthAccountRole();
        accountRole.setAccountId(account.getId());
        accountRole.setRoleId(userRole.getId());
        accountRoleMapper.insert(accountRole);

        DemoAuthProfile profile = DemoAuthProfile.create(account.getId(), nickname, email);
        profileMapper.insert(profile);
        return new RegisteredAccount(account, profile);
    }

    public record RegisteredAccount(DemoAuthAccount account, DemoAuthProfile profile) {
    }
}
