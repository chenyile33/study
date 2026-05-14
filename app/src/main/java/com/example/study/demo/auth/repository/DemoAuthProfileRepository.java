package com.example.study.demo.auth.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.core.page.PageParam;
import com.example.study.demo.auth.entity.DemoAuthAccount;
import com.example.study.demo.auth.entity.DemoAuthProfile;
import com.example.study.demo.auth.mapper.DemoAuthAccountMapper;
import com.example.study.demo.auth.mapper.DemoAuthProfileMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 认证资料查询仓库。
 */
@Repository
public class DemoAuthProfileRepository {

    private final DemoAuthProfileMapper profileMapper;
    private final DemoAuthAccountMapper accountMapper;

    public DemoAuthProfileRepository(DemoAuthProfileMapper profileMapper, DemoAuthAccountMapper accountMapper) {
        this.profileMapper = profileMapper;
        this.accountMapper = accountMapper;
    }

    public Page<ProfileRecord> page(PageParam pageParam, String keyword) {
        Page<DemoAuthProfile> profilePage = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());
        Page<DemoAuthProfile> resultPage = profileMapper.selectPage(profilePage, buildProfileQuery(keyword));
        List<DemoAuthProfile> profiles = resultPage.getRecords();
        Map<Long, DemoAuthAccount> accounts = loadAccounts(profiles);
        List<ProfileRecord> records = profiles.stream()
                .map(profile -> new ProfileRecord(accounts.get(profile.getAccountId()), profile))
                .filter(ProfileRecord::hasAccount)
                .toList();

        Page<ProfileRecord> page = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        page.setRecords(records);
        return page;
    }

    private LambdaQueryWrapper<DemoAuthProfile> buildProfileQuery(String keyword) {
        LambdaQueryWrapper<DemoAuthProfile> queryWrapper = new LambdaQueryWrapper<>();
        String normalizedKeyword = normalize(keyword);
        if (!normalizedKeyword.isEmpty()) {
            List<Long> matchingAccountIds = loadAccountIdsByUsername(normalizedKeyword);
            queryWrapper.and(wrapper -> wrapper
                    .like(DemoAuthProfile::getNickname, normalizedKeyword)
                    .or()
                    .like(DemoAuthProfile::getEmail, normalizedKeyword));
            if (!matchingAccountIds.isEmpty()) {
                queryWrapper.or(wrapper -> wrapper.in(DemoAuthProfile::getAccountId, matchingAccountIds));
            }
        }
        queryWrapper.orderByDesc(DemoAuthProfile::getId);
        return queryWrapper;
    }

    private List<Long> loadAccountIdsByUsername(String keyword) {
        if (keyword.isEmpty()) {
            return List.of();
        }
        List<DemoAuthAccount> accounts = accountMapper.selectList(new LambdaQueryWrapper<DemoAuthAccount>()
                .like(DemoAuthAccount::getUsername, keyword));
        if (accounts.isEmpty()) {
            return List.of();
        }

        List<Long> accountIds = new ArrayList<>(accounts.size());
        for (DemoAuthAccount account : accounts) {
            accountIds.add(account.getId());
        }
        return accountIds;
    }

    private Map<Long, DemoAuthAccount> loadAccounts(List<DemoAuthProfile> profiles) {
        List<Long> accountIds = profiles.stream()
                .map(DemoAuthProfile::getAccountId)
                .distinct()
                .toList();
        if (accountIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, DemoAuthAccount> accounts = new LinkedHashMap<>();
        accountMapper.selectList(new LambdaQueryWrapper<DemoAuthAccount>()
                        .in(DemoAuthAccount::getId, accountIds))
                .forEach(account -> accounts.put(account.getId(), account));
        return accounts;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public record ProfileRecord(DemoAuthAccount account, DemoAuthProfile profile) {

        private boolean hasAccount() {
            return account != null;
        }
    }
}
