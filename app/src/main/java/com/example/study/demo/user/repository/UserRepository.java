package com.example.study.demo.user.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.core.page.PageParam;
import com.example.study.demo.user.entity.UserProfile;
import com.example.study.demo.user.mapper.UserProfileMapper;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Optional;

/**
 * 用户 Demo 的数据库访问层，替代早期的内存仓库。
 */
@Repository
public class UserRepository {

    private final UserProfileMapper userProfileMapper;

    public UserRepository(UserProfileMapper userProfileMapper) {
        this.userProfileMapper = userProfileMapper;
    }

    public UserProfile save(String username, String nickname, String email) {
        UserProfile userProfile = UserProfile.create(username, nickname, email);
        userProfileMapper.insert(userProfile);
        return userProfile;
    }

    public Optional<UserProfile> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(userProfileMapper.selectById(id));
    }

    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        Long count = userProfileMapper.selectCount(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUsername, username.trim()));
        return count != null && count > 0;
    }

    public Page<UserProfile> page(PageParam pageParam, String keyword) {
        Page<UserProfile> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());
        return userProfileMapper.selectPage(page, buildPageQuery(keyword));
    }

    private LambdaQueryWrapper<UserProfile> buildPageQuery(String keyword) {
        LambdaQueryWrapper<UserProfile> queryWrapper = new LambdaQueryWrapper<>();
        String normalizedKeyword = normalize(keyword);
        if (!normalizedKeyword.isEmpty()) {
            // 这里保留和原内存版本一致的搜索语义：用户名或昵称命中即可。
            queryWrapper.and(wrapper -> wrapper
                    .like(UserProfile::getUsername, normalizedKeyword)
                    .or()
                    .like(UserProfile::getNickname, normalizedKeyword));
        }
        queryWrapper.orderByDesc(UserProfile::getId);
        return queryWrapper;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
