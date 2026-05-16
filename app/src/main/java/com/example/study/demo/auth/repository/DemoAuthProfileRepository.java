package com.example.study.demo.auth.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.core.page.PageParam;
import com.example.study.demo.auth.domain.AuthProfileRecord;
import com.example.study.demo.auth.mapper.DemoAuthProfileMapper;
import org.springframework.stereotype.Repository;

import java.util.Locale;

/**
 * 认证资料查询仓库，复杂查询统一走 Mapper XML。
 */
@Repository
public class DemoAuthProfileRepository {

    private final DemoAuthProfileMapper profileMapper;

    public DemoAuthProfileRepository(DemoAuthProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }

    public Page<AuthProfileRecord> page(PageParam pageParam, String keyword) {
        Page<AuthProfileRecord> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());
        return profileMapper.selectProfilePage(page, normalize(keyword));
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
