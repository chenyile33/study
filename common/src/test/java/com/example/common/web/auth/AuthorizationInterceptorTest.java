package com.example.common.web.auth;

import com.example.common.core.auth.AuthContext;
import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.auth.AuthScope;
import com.example.common.core.auth.authorization.AuthorizationMode;
import com.example.common.core.auth.authorization.DefaultAuthorizationChecker;
import com.example.common.core.auth.authorization.RequirePermissions;
import com.example.common.core.auth.authorization.RequireRoles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorizationInterceptorTest {

    private final AuthorizationInterceptor interceptor = new AuthorizationInterceptor(
            enabledProperties(),
            new DefaultAuthorizationChecker()
    );

    @AfterEach
    void clearContext() {
        AuthContext.clear();
    }

    @Test
    void shouldPassWhenHandlerHasNoAuthorizationAnnotation() throws Exception {
        HandlerMethod handlerMethod = handlerMethod("publicEndpoint");

        assertTrue(interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod));
    }

    @Test
    void shouldRequireLoginWhenEndpointHasAuthorizationAnnotation() throws Exception {
        HandlerMethod handlerMethod = handlerMethod("adminEndpoint");

        AuthException exception = assertThrows(AuthException.class,
                () -> interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod));

        assertEquals(AuthErrorCode.UNAUTHORIZED.getCode(), exception.getCode());
    }

    @Test
    void shouldPassWhenPrincipalHasRequiredRole() throws Exception {
        HandlerMethod handlerMethod = handlerMethod("adminEndpoint");
        AuthPrincipal principal = principal(List.of("ADMIN"), List.of());

        try (AuthScope ignored = AuthContext.open(principal)) {
            assertTrue(interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod));
        }
    }

    @Test
    void shouldThrowForbiddenWhenRoleDoesNotMatch() throws Exception {
        HandlerMethod handlerMethod = handlerMethod("adminEndpoint");
        AuthPrincipal principal = principal(List.of("USER"), List.of());

        try (AuthScope ignored = AuthContext.open(principal)) {
            AuthException exception = assertThrows(AuthException.class,
                    () -> interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod));

            assertEquals(AuthErrorCode.FORBIDDEN.getCode(), exception.getCode());
        }
    }

    @Test
    void shouldPassWhenPrincipalHasRequiredPermission() throws Exception {
        HandlerMethod handlerMethod = handlerMethod("readEndpoint");
        AuthPrincipal principal = principal(List.of("USER"), List.of("secure:read"));

        try (AuthScope ignored = AuthContext.open(principal)) {
            assertTrue(interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod));
        }
    }

    @Test
    void shouldThrowForbiddenWhenPermissionDoesNotMatch() throws Exception {
        HandlerMethod handlerMethod = handlerMethod("readEndpoint");
        AuthPrincipal principal = principal(List.of("USER"), List.of("other:read"));

        try (AuthScope ignored = AuthContext.open(principal)) {
            AuthException exception = assertThrows(AuthException.class,
                    () -> interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod));

            assertEquals(AuthErrorCode.FORBIDDEN.getCode(), exception.getCode());
        }
    }

    @Test
    void methodAnnotationShouldOverrideClassAnnotation() throws Exception {
        AuthorizationInterceptor classInterceptor = new AuthorizationInterceptor(
                enabledProperties(),
                new DefaultAuthorizationChecker()
        );
        HandlerMethod handlerMethod = new HandlerMethod(
                new ClassLevelController(),
                ClassLevelController.class.getDeclaredMethod("userEndpoint")
        );
        AuthPrincipal principal = principal(List.of("USER"), List.of());

        try (AuthScope ignored = AuthContext.open(principal)) {
            assertTrue(classInterceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod));
        }
    }

    @Test
    void disabledAuthShouldAlwaysPass() throws Exception {
        CommonAuthProperties properties = new CommonAuthProperties();
        properties.setEnabled(false);
        AuthorizationInterceptor disabledInterceptor = new AuthorizationInterceptor(
                properties,
                new DefaultAuthorizationChecker()
        );

        assertTrue(disabledInterceptor.preHandle(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                handlerMethod("adminEndpoint")
        ));
    }

    private static CommonAuthProperties enabledProperties() {
        CommonAuthProperties properties = new CommonAuthProperties();
        properties.setEnabled(true);
        return properties;
    }

    private static AuthPrincipal principal(List<String> roles, List<String> permissions) {
        return AuthPrincipal.of("1", "tester", roles, permissions, Map.of());
    }

    private static HandlerMethod handlerMethod(String methodName) throws NoSuchMethodException {
        Method method = DemoController.class.getDeclaredMethod(methodName);
        return new HandlerMethod(new DemoController(), method);
    }

    private static class DemoController {

        void publicEndpoint() {
        }

        @RequireRoles("ADMIN")
        void adminEndpoint() {
        }

        @RequirePermissions(value = "secure:read", mode = AuthorizationMode.ALL)
        void readEndpoint() {
        }
    }

    @RequireRoles("ADMIN")
    private static class ClassLevelController {

        @RequireRoles("USER")
        void userEndpoint() {
        }
    }
}
