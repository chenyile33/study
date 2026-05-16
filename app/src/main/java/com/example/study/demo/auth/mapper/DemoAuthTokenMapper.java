package com.example.study.demo.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.study.demo.auth.entity.DemoAuthToken;
import org.apache.ibatis.annotations.Param;

/**
 * token 表 Mapper，用于保存和删除 opaque token 状态。
 */
public interface DemoAuthTokenMapper extends BaseMapper<DemoAuthToken> {

    DemoAuthToken selectByToken(@Param("token") String token);
}
