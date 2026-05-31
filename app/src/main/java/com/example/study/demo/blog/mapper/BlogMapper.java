package com.example.study.demo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.study.demo.blog.domain.Blog;
import org.apache.ibatis.annotations.Param;

public interface BlogMapper extends BaseMapper<Blog> {

    Blog selectBlogById(@Param("id") Long id);

    Page<Blog> selectBlogPage(Page<Blog> page, @Param("keyword") String keyword);
}
