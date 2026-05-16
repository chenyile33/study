package com.example.study.demo.auth.repository;

import com.example.study.demo.auth.domain.DemoAccount;
import com.example.study.demo.auth.entity.DemoAuthAccount;
import com.example.study.demo.auth.entity.DemoAuthRole;
import com.example.study.demo.auth.mapper.DemoAuthAccountMapper;
import com.example.study.demo.auth.mapper.DemoAuthPermissionMapper;
import com.example.study.demo.auth.mapper.DemoAuthRoleMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 从数据库读取账号、角色和权限，用来组装登录后的认证主体。
 */
@Repository
public class DemoAccountRepository {

    private final DemoAuthAccountMapper accountMapper;
    private final DemoAuthRoleMapper roleMapper;
    private final DemoAuthPermissionMapper permissionMapper;

    public DemoAccountRepository(
            DemoAuthAccountMapper accountMapper,
            DemoAuthRoleMapper roleMapper,
            DemoAuthPermissionMapper permissionMapper
    ) {
        this.accountMapper = accountMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    public Optional<DemoAccount> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        // 登录入口只接受启用账号，停用账号等价于不存在；具体 SQL 写在 XML 中。
        DemoAuthAccount account = accountMapper.selectEnabledByUsername(username.trim());
        return toDemoAccount(account);
    }

    public Optional<DemoAccount> findById(Long accountId) {
        if (accountId == null || accountId <= 0) {
            return Optional.empty();
        }
        DemoAuthAccount account = accountMapper.selectEnabledById(accountId);
        return toDemoAccount(account);
    }

    private Optional<DemoAccount> toDemoAccount(DemoAuthAccount account) {
        if (account == null) {
            return Optional.empty();
        }

        // 账号表只保存登录信息，认证主体需要临时聚合角色和权限快照。
        List<DemoAuthRole> roles = loadRoles(account.getId());
        List<Long> roleIds = roles.stream()
                .map(DemoAuthRole::getId)
                .toList();
        List<String> roleCodes = roles.stream()
                .map(DemoAuthRole::getRoleCode)
                .toList();
        List<String> permissionCodes = loadPermissions(roleIds);

        return Optional.of(new DemoAccount(
                String.valueOf(account.getId()),
                account.getUsername(),
                account.getPassword(),
                roleCodes,
                permissionCodes
        ));
    }

    private List<DemoAuthRole> loadRoles(Long accountId) {
        // 角色查询放到 XML，学习真实项目里常见的 join 写法。
        return roleMapper.selectByAccountId(accountId);
    }

    private List<String> loadPermissions(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return List.of();
        }

        // 权限来自角色，不直接绑在账号上，方便后续演示角色授权变更。
        return permissionMapper.selectPermissionCodesByRoleIds(roleIds);
    }
}
