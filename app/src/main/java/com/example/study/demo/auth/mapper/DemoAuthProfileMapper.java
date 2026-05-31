package com.example.study.demo.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.study.demo.auth.domain.AuthProfileRecord;
import com.example.study.demo.auth.entity.DemoAuthProfile;
import org.apache.ibatis.annotations.Param;

/**
 * 认证资料表 Mapper。
 */
public interface DemoAuthProfileMapper extends BaseMapper<DemoAuthProfile> {

    Page<AuthProfileRecord> selectProfilePage(Page<AuthProfileRecord> page, @Param("keyword") String keyword);
}
