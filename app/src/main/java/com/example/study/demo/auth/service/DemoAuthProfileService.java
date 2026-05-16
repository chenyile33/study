package com.example.study.demo.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.core.page.PageParam;
import com.example.common.core.page.PageResult;
import com.example.study.demo.auth.domain.AuthProfileRecord;
import com.example.study.demo.auth.dto.AuthProfileResponse;
import com.example.study.demo.auth.repository.DemoAuthProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoAuthProfileService {

    private final DemoAuthProfileRepository profileRepository;

    public DemoAuthProfileService(DemoAuthProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public PageResult<AuthProfileResponse> pageProfiles(PageParam pageParam, String keyword) {
        PageParam normalizedPageParam = pageParam == null ? new PageParam() : pageParam;
        Page<AuthProfileRecord> profilePage = profileRepository.page(normalizedPageParam, keyword);
        List<AuthProfileResponse> records = profilePage.getRecords().stream()
                .map(AuthProfileResponse::from)
                .toList();
        return PageResult.of(records, profilePage.getTotal(), profilePage.getCurrent(), profilePage.getSize());
    }
}
