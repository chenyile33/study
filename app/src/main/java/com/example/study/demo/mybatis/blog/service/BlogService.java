package com.example.study.demo.mybatis.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.common.core.page.PageParam;
import com.example.common.core.page.PageResult;
import com.example.common.core.util.AssertUtils;
import com.example.study.demo.mybatis.blog.domain.Blog;
import com.example.study.demo.mybatis.blog.dto.BlogResponse;
import com.example.study.demo.mybatis.blog.dto.CreateBlogRequest;
import com.example.study.demo.mybatis.blog.dto.UpdateBlogRequest;
import com.example.study.demo.mybatis.blog.mapper.BlogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogService {

    private final BlogMapper blogMapper;

    public BlogService(BlogMapper blogMapper) {
        this.blogMapper = blogMapper;
    }

    public BlogResponse createBlog(CreateBlogRequest request) {
        AssertUtils.notNull(request, CommonErrorCode.PARAM_ERROR, "请求体不能为空");

        Blog blog = new Blog();
        applyCreateFields(blog, request);

        LocalDateTime now = LocalDateTime.now();
        blog.setCreateTime(now);
        blog.setUpdateTime(now);
        blog.setViews(request.getViews() == null ? 0 : request.getViews());

        blogMapper.insert(blog);
        return BlogResponse.from(blog);
    }

    public BlogResponse getBlog(Long id) {
        return BlogResponse.from(getRequiredBlog(id));
    }

    public PageResult<BlogResponse> pageBlogs(PageParam pageParam, String keyword) {
        PageParam normalizedPageParam = pageParam == null ? new PageParam() : pageParam;
        Page<Blog> page = new Page<>(normalizedPageParam.getPageNum(), normalizedPageParam.getPageSize());
        Page<Blog> resultPage = blogMapper.selectPage(page, buildPageQuery(keyword));
        List<BlogResponse> records = resultPage.getRecords().stream()
                .map(BlogResponse::from)
                .toList();

        return PageResult.of(records, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    public BlogResponse updateBlog(Long id, UpdateBlogRequest request) {
        AssertUtils.notNull(request, CommonErrorCode.PARAM_ERROR, "请求体不能为空");
        Blog blog = getRequiredBlog(id);
        applyUpdateFields(blog, request);
        blog.setUpdateTime(LocalDateTime.now());

        blogMapper.updateById(blog);
        return BlogResponse.from(getRequiredBlog(id));
    }

    public void deleteBlog(Long id) {
        validateId(id);
        int rows = blogMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "博客不存在");
        }
    }

    private Blog getRequiredBlog(Long id) {
        validateId(id);
        Blog blog = blogMapper.selectById(id);
        if (blog == null) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "博客不存在");
        }
        return blog;
    }

    private LambdaQueryWrapper<Blog> buildPageQuery(String keyword) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        String normalizedKeyword = normalize(keyword);
        if (!normalizedKeyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Blog::getTitle, normalizedKeyword)
                    .or()
                    .like(Blog::getDescription, normalizedKeyword)
                    .or()
                    .like(Blog::getContent, normalizedKeyword)
            );
        }
        queryWrapper.orderByDesc(Blog::getUpdateTime).orderByDesc(Blog::getId);
        return queryWrapper;
    }

    private void applyCreateFields(Blog blog, CreateBlogRequest request) {
        blog.setTitle(request.getTitle().trim());
        blog.setContent(request.getContent());
        blog.setDescription(request.getDescription());
        blog.setFirstPicture(request.getFirstPicture());
        blog.setFlag(request.getFlag());
        blog.setAppreciation(request.getAppreciation());
        blog.setCommentabled(request.getCommentabled());
        blog.setPublished(request.getPublished());
        blog.setRecommend(request.getRecommend());
        blog.setShareStatement(request.getShareStatement());
        blog.setTypeId(request.getTypeId());
        blog.setUserId(request.getUserId());
    }

    private void applyUpdateFields(Blog blog, UpdateBlogRequest request) {
        if (request.getTitle() != null) {
            AssertUtils.hasText(request.getTitle(), CommonErrorCode.PARAM_ERROR, "title不能为空");
            blog.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null) {
            blog.setContent(request.getContent());
        }
        if (request.getDescription() != null) {
            blog.setDescription(request.getDescription());
        }
        if (request.getFirstPicture() != null) {
            blog.setFirstPicture(request.getFirstPicture());
        }
        if (request.getFlag() != null) {
            blog.setFlag(request.getFlag());
        }
        if (request.getAppreciation() != null) {
            blog.setAppreciation(request.getAppreciation());
        }
        if (request.getCommentabled() != null) {
            blog.setCommentabled(request.getCommentabled());
        }
        if (request.getPublished() != null) {
            blog.setPublished(request.getPublished());
        }
        if (request.getRecommend() != null) {
            blog.setRecommend(request.getRecommend());
        }
        if (request.getShareStatement() != null) {
            blog.setShareStatement(request.getShareStatement());
        }
        if (request.getViews() != null) {
            blog.setViews(request.getViews());
        }
        if (request.getTypeId() != null) {
            blog.setTypeId(request.getTypeId());
        }
        if (request.getUserId() != null) {
            blog.setUserId(request.getUserId());
        }
    }

    private void validateId(Long id) {
        AssertUtils.notNull(id, CommonErrorCode.PARAM_ERROR, "id不能为空");
        AssertUtils.isTrue(id > 0, CommonErrorCode.PARAM_ERROR, "id必须大于0");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
