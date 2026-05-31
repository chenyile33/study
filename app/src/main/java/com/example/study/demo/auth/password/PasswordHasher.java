package com.example.study.demo.auth.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * app 层密码哈希工具，当前 Demo 使用 BCrypt 保存和校验密码。
 */
@Component
public class PasswordHasher {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String passwordHash) {
        if (rawPassword == null || passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}
