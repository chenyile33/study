package com.example.study.demo.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.study.demo.auth.entity.DemoAuthPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限码表 Mapper。
 */
public interface DemoAuthPermissionMapper extends BaseMapper<DemoAuthPermission> {

    List<String> selectPermissionCodesByRoleIds(@Param("roleIds") List<Long> roleIds);
}
