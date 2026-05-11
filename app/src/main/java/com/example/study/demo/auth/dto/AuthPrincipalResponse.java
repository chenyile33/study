package com.example.study.demo.auth.dto;

import com.example.common.core.auth.AuthPrincipal;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * 对外返回的当前认证主体信息。
 */
@Getter
public class AuthPrincipalResponse {

    private final String principalId;
    private final String principalName;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final Map<String, String> attributes;

    private AuthPrincipalResponse(String principalId, String principalName, Set<String> roles, Set<String> permissions,
                                  Map<String, String> attributes) {
        this.principalId = principalId;
        this.principalName = principalName;
        this.roles = roles;
        this.permissions = permissions;
        this.attributes = attributes;
    }

    public static AuthPrincipalResponse from(AuthPrincipal principal) {
        return new AuthPrincipalResponse(
                principal.getPrincipalId(),
                principal.getPrincipalName(),
                principal.getRoles(),
                principal.getPermissions(),
                principal.getAttributes()
        );
    }

}
