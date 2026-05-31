package com.example.study.demo.auth.password;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    void hashShouldNotExposeRawPasswordAndShouldMatchOriginalPassword() {
        String rawPassword = "admin123";

        String passwordHash = passwordHasher.hash(rawPassword);

        assertNotEquals(rawPassword, passwordHash);
        assertTrue(passwordHasher.matches(rawPassword, passwordHash));
    }

    @Test
    void matchesShouldRejectWrongOrBlankInput() {
        String passwordHash = passwordHasher.hash("alice123");

        assertFalse(passwordHasher.matches("wrong-password", passwordHash));
        assertFalse(passwordHasher.matches(null, passwordHash));
        assertFalse(passwordHasher.matches("alice123", null));
        assertFalse(passwordHasher.matches("alice123", " "));
    }
}
