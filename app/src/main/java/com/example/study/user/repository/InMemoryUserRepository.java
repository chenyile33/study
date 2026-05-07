package com.example.study.user.repository;

import com.example.study.user.domain.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository {

    private final AtomicLong idGenerator = new AtomicLong(1000);
    private final ConcurrentMap<Long, UserProfile> users = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        save("alice", "Alice", "alice@example.com");
        save("bob", "Bob", "bob@example.com");
        save("carol", "Carol", "carol@example.com");
    }

    public UserProfile save(String username, String nickname, String email) {
        UserProfile userProfile = UserProfile.create(idGenerator.incrementAndGet(), username, nickname, email);
        users.put(userProfile.getId(), userProfile);
        return userProfile;
    }

    public Optional<UserProfile> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public boolean existsByUsername(String username) {
        String normalizedUsername = normalize(username);
        return users.values().stream()
                .map(UserProfile::getUsername)
                .map(InMemoryUserRepository::normalize)
                .anyMatch(normalizedUsername::equals);
    }

    public List<UserProfile> search(String keyword) {
        String normalizedKeyword = normalize(keyword);
        return users.values().stream()
                .filter(userProfile -> matches(userProfile, normalizedKeyword))
                .sorted(Comparator.comparing(UserProfile::getId).reversed())
                .toList();
    }

    private static boolean matches(UserProfile userProfile, String normalizedKeyword) {
        if (normalizedKeyword.isEmpty()) {
            return true;
        }
        return normalize(userProfile.getUsername()).contains(normalizedKeyword)
                || normalize(userProfile.getNickname()).contains(normalizedKeyword);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
