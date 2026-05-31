package com.example.study.demo.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.study.demo.auth.entity.DemoAuthRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表 Mapper。
 */
public interface DemoAuthRoleMapper extends BaseMapper<DemoAuthRole> {

    List<DemoAuthRole> selectByAccountId(@Param("accountId") Long accountId);

    DemoAuthRole selectByRoleCode(@Param("roleCode") String roleCode);
}
