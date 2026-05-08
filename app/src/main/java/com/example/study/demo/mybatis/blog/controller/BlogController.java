package com.example.study.demo.mybatis.blog.controller;

import com.example.common.core.page.PageParam;
import com.example.common.core.page.PageResult;
import com.example.common.core.result.Result;
import com.example.study.demo.mybatis.blog.dto.BlogResponse;
import com.example.study.demo.mybatis.blog.dto.CreateBlogRequest;
import com.example.study.demo.mybatis.blog.dto.UpdateBlogRequest;
import com.example.study.demo.mybatis.blog.service.BlogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mybatis/blogs")
public class BlogController {

    private static final Logger log = LoggerFactory.getLogger(BlogController.class);

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping
    public Result<BlogResponse> createBlog(@Valid @RequestBody(required = false) CreateBlogRequest request) {
        log.info("创建博客，title={}", request == null ? null : request.getTitle());
        return Result.success(blogService.createBlog(request));
    }

    @GetMapping("/{id}")
    public Result<BlogResponse> getBlog(@PathVariable Long id) {
        log.info("查询博客详情，id={}", id);
        return Result.success(blogService.getBlog(id));
    }

    @GetMapping
    public Result<PageResult<BlogResponse>> pageBlogs(
            @ModelAttribute PageParam pageParam,
            @RequestParam(required = false) String keyword
    ) {
        log.info("分页查询博客，pageNum={}, pageSize={}, keyword={}",
                pageParam.getPageNum(), pageParam.getPageSize(), keyword);
        return Result.success(blogService.pageBlogs(pageParam, keyword));
    }

    @PutMapping("/{id}")
    public Result<BlogResponse> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) UpdateBlogRequest request
    ) {
        log.info("更新博客，id={}", id);
        return Result.success(blogService.updateBlog(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBlog(@PathVariable Long id) {
        log.info("删除博客，id={}", id);
        blogService.deleteBlog(id);
        return Result.success();
    }
}
