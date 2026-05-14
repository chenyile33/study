package com.example.common.web.auth;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CommonAuthPropertiesTest {

    @Test
    void shouldKeepSafeDefaults() {
        CommonAuthProperties properties = new CommonAuthProperties();

        assertFalse(properties.isEnabled());
        assertEquals("Authorization", properties.getTokenHeader());
        assertEquals("Bearer", properties.getTokenPrefix());
        assertEquals(List.of(), properties.getPermitPaths());
    }

    @Test
    void shouldNormalizePermitPaths() {
        CommonAuthProperties properties = new CommonAuthProperties();

        properties.setPermitPaths(List.of("api/auth/login", "/api/auth/login", " ", "/actuator/health"));

        assertEquals(List.of("/api/auth/login", "/actuator/health"), properties.getPermitPaths());
    }
}
