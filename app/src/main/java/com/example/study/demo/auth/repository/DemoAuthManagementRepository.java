package com.example.study.demo.auth.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.study.demo.auth.entity.DemoAuthAccount;
import com.example.study.demo.auth.entity.DemoAuthAccountRole;
import com.example.study.demo.auth.entity.DemoAuthPermission;
import com.example.study.demo.auth.entity.DemoAuthRole;
import com.example.study.demo.auth.entity.DemoAuthRolePermission;
import com.example.study.demo.auth.mapper.DemoAuthAccountMapper;
import com.example.study.demo.auth.mapper.DemoAuthAccountRoleMapper;
import com.example.study.demo.auth.mapper.DemoAuthPermissionMapper;
import com.example.study.demo.auth.mapper.DemoAuthRoleMapper;
import com.example.study.demo.auth.mapper.DemoAuthRolePermissionMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 认证管理 Demo 的数据库访问层。
 */
@Repository
public class DemoAuthManagementRepository {

    private final DemoAuthAccountMapper accountMapper;
    private final DemoAuthRoleMapper roleMapper;
    private final DemoAuthPermissionMapper permissionMapper;
    private final DemoAuthAccountRoleMapper accountRoleMapper;
    private final DemoAuthRolePermissionMapper rolePermissionMapper;

    public DemoAuthManagementRepository(DemoAuthAccountMapper accountMapper,
                                        DemoAuthRoleMapper roleMapper,
                                        DemoAuthPermissionMapper permissionMapper,
                                        DemoAuthAccountRoleMapper accountRoleMapper,
                                        DemoAuthRolePermissionMapper rolePermissionMapper) {
        this.accountMapper = accountMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.accountRoleMapper = accountRoleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    public Optional<DemoAuthAccount> findAccountById(Long accountId) {
        return Optional.ofNullable(accountMapper.selectById(accountId));
    }

    public int updateAccountEnabled(Long accountId, boolean enabled) {
        DemoAuthAccount account = new DemoAuthAccount();
        account.setId(accountId);
        account.setEnabled(enabled);
        account.setUpdateTime(LocalDateTime.now());
        return accountMapper.updateById(account);
    }

    public List<DemoAuthRole> findRolesByAccountId(Long accountId) {
        return roleMapper.selectByAccountId(accountId);
    }

    public List<DemoAuthPermission> findPermissionsByRoleIds(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return permissionMapper.selectPermissionsByRoleIds(roleIds);
    }

    public List<DemoAuthRole> listRoles() {
        return roleMapper.selectList(new QueryWrapper<DemoAuthRole>().orderByAsc("id"));
    }

    public List<DemoAuthPermission> listPermissions() {
        return permissionMapper.selectList(new QueryWrapper<DemoAuthPermission>().orderByAsc("id"));
    }

    public List<DemoAuthRole> findRolesByCodes(List<String> roleCodes) {
        return roleMapper.selectList(new QueryWrapper<DemoAuthRole>()
                .in("role_code", roleCodes)
                .orderByAsc("id"));
    }

    public Optional<DemoAuthRole> findRoleByCode(String roleCode) {
        return Optional.ofNullable(roleMapper.selectByRoleCode(roleCode));
    }

    public List<DemoAuthPermission> findPermissionsByCodes(List<String> permissionCodes) {
        return permissionMapper.selectList(new QueryWrapper<DemoAuthPermission>()
                .in("permission_code", permissionCodes)
                .orderByAsc("id"));
    }

    public void replaceAccountRoles(Long accountId, List<DemoAuthRole> roles) {
        accountRoleMapper.delete(new QueryWrapper<DemoAuthAccountRole>().eq("account_id", accountId));
        for (DemoAuthRole role : roles) {
            DemoAuthAccountRole accountRole = new DemoAuthAccountRole();
            accountRole.setAccountId(accountId);
            accountRole.setRoleId(role.getId());
            accountRoleMapper.insert(accountRole);
        }
    }

    public void replaceRolePermissions(Long roleId, List<DemoAuthPermission> permissions) {
        rolePermissionMapper.delete(new QueryWrapper<DemoAuthRolePermission>().eq("role_id", roleId));
        for (DemoAuthPermission permission : permissions) {
            DemoAuthRolePermission rolePermission = new DemoAuthRolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permission.getId());
            rolePermissionMapper.insert(rolePermission);
        }
    }
}
