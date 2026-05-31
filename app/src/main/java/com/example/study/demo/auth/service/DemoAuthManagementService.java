package com.example.study.demo.auth.service;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.common.core.util.AssertUtils;
import com.example.study.demo.auth.dto.AssignAccountRolesRequest;
import com.example.study.demo.auth.dto.AssignRolePermissionsRequest;
import com.example.study.demo.auth.dto.AuthAccountManagementResponse;
import com.example.study.demo.auth.dto.AuthPermissionResponse;
import com.example.study.demo.auth.dto.AuthRoleResponse;
import com.example.study.demo.auth.dto.UpdateAccountEnabledRequest;
import com.example.study.demo.auth.entity.DemoAuthAccount;
import com.example.study.demo.auth.entity.DemoAuthPermission;
import com.example.study.demo.auth.entity.DemoAuthRole;
import com.example.study.demo.auth.repository.DemoAuthManagementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证管理 Demo：管理账号状态、账号角色和角色权限。
 */
@Service
public class DemoAuthManagementService {

    private final DemoAuthManagementRepository managementRepository;

    public DemoAuthManagementService(DemoAuthManagementRepository managementRepository) {
        this.managementRepository = managementRepository;
    }

    @Transactional(readOnly = true)
    public AuthAccountManagementResponse getAccount(Long accountId) {
        return toAccountResponse(getRequiredAccount(accountId));
    }

    @Transactional
    public AuthAccountManagementResponse updateAccountEnabled(Long accountId, UpdateAccountEnabledRequest request) {
        validateId(accountId);
        AssertUtils.notNull(request, CommonErrorCode.PARAM_ERROR, "请求体不能为空");
        AssertUtils.notNull(request.getEnabled(), CommonErrorCode.PARAM_ERROR, "enabled不能为空");
        getRequiredAccount(accountId);

        int rows = managementRepository.updateAccountEnabled(accountId, request.getEnabled());
        if (rows == 0) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "账号不存在");
        }
        return getAccount(accountId);
    }

    @Transactional
    public AuthAccountManagementResponse assignAccountRoles(Long accountId, AssignAccountRolesRequest request) {
        validateId(accountId);
        AssertUtils.notNull(request, CommonErrorCode.PARAM_ERROR, "请求体不能为空");
        getRequiredAccount(accountId);

        List<String> roleCodes = normalizeCodes(request.getRoleCodes(), "roleCodes");
        List<DemoAuthRole> roles = managementRepository.findRolesByCodes(roleCodes);
        Set<String> existingRoleCodes = roles.stream()
                .map(DemoAuthRole::getRoleCode)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        assertCodesExist(roleCodes, existingRoleCodes, "角色");

        managementRepository.replaceAccountRoles(accountId, roles);
        return getAccount(accountId);
    }

    @Transactional(readOnly = true)
    public List<AuthRoleResponse> listRoles() {
        return managementRepository.listRoles().stream()
                .map(AuthRoleResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuthPermissionResponse> listPermissions() {
        return managementRepository.listPermissions().stream()
                .map(AuthPermissionResponse::from)
                .toList();
    }

    @Transactional
    public List<AuthPermissionResponse> assignRolePermissions(String roleCode, AssignRolePermissionsRequest request) {
        AssertUtils.hasText(roleCode, CommonErrorCode.PARAM_ERROR, "roleCode不能为空");
        AssertUtils.notNull(request, CommonErrorCode.PARAM_ERROR, "请求体不能为空");

        DemoAuthRole role = managementRepository.findRoleByCode(roleCode.trim())
                .orElseThrow(() -> new BusinessException(CommonErrorCode.PARAM_ERROR, "角色不存在"));
        List<String> permissionCodes = normalizeCodes(request.getPermissionCodes(), "permissionCodes");
        List<DemoAuthPermission> permissions = managementRepository.findPermissionsByCodes(permissionCodes);
        Set<String> existingPermissionCodes = permissions.stream()
                .map(DemoAuthPermission::getPermissionCode)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        assertCodesExist(permissionCodes, existingPermissionCodes, "权限码");

        managementRepository.replaceRolePermissions(role.getId(), permissions);
        return permissions.stream()
                .map(AuthPermissionResponse::from)
                .toList();
    }

    private AuthAccountManagementResponse toAccountResponse(DemoAuthAccount account) {
        List<DemoAuthRole> roles = managementRepository.findRolesByAccountId(account.getId());
        List<Long> roleIds = roles.stream()
                .map(DemoAuthRole::getId)
                .toList();
        List<DemoAuthPermission> permissions = managementRepository.findPermissionsByRoleIds(roleIds);
        return AuthAccountManagementResponse.from(account, roles, permissions);
    }

    private DemoAuthAccount getRequiredAccount(Long accountId) {
        validateId(accountId);
        return managementRepository.findAccountById(accountId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.PARAM_ERROR, "账号不存在"));
    }

    private static void validateId(Long id) {
        AssertUtils.notNull(id, CommonErrorCode.PARAM_ERROR, "accountId" + "不能为空");
        AssertUtils.isTrue(id > 0, CommonErrorCode.PARAM_ERROR, "accountId" + "必须大于0");
    }

    private static List<String> normalizeCodes(List<String> codes, String fieldName) {
        AssertUtils.notEmpty(codes, CommonErrorCode.PARAM_ERROR, fieldName + "不能为空");
        Set<String> normalized = new LinkedHashSet<>();
        for (String code : codes) {
            AssertUtils.hasText(code, CommonErrorCode.PARAM_ERROR, fieldName + "不能包含空值");
            normalized.add(code.trim());
        }
        return List.copyOf(normalized);
    }

    private static void assertCodesExist(List<String> requestedCodes, Set<String> existingCodes, String label) {
        List<String> missingCodes = requestedCodes.stream()
                .filter(code -> !existingCodes.contains(code))
                .toList();
        if (!missingCodes.isEmpty()) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, label + "不存在: " + String.join(", ", missingCodes));
        }
    }
}
