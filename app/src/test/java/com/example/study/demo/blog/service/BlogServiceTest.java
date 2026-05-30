package com.example.study.demo.blog.service;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.study.demo.blog.dto.CreateBlogRequest;
import com.example.study.demo.blog.mapper.BlogMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class BlogServiceTest {

    private final BlogMapper blogMapper = mock(BlogMapper.class);
    private final BlogService blogService = new BlogService(blogMapper);

    @Test
    void createBlogShouldRejectNullRequestBeforeMapper() {
        BusinessException exception = assertThrows(BusinessException.class, () -> blogService.createBlog(null, null));

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        verifyNoInteractions(blogMapper);
    }

    @Test
    void createBlogShouldRejectBlankTitleBeforeMapper() {
        CreateBlogRequest request = validCreateRequest();
        request.setTitle(" ");

        BusinessException exception = assertThrows(BusinessException.class, () -> blogService.createBlog(request, null));

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        verifyNoInteractions(blogMapper);
    }

    @Test
    void createBlogShouldRejectMissingRequiredFlagBeforeMapper() {
        CreateBlogRequest request = validCreateRequest();
        request.setPublished(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> blogService.createBlog(request, null));

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        verifyNoInteractions(blogMapper);
    }

    private static CreateBlogRequest validCreateRequest() {
        CreateBlogRequest request = new CreateBlogRequest();
        request.setTitle("MyBatis note");
        request.setAppreciation(false);
        request.setCommentabled(true);
        request.setPublished(true);
        request.setRecommend(false);
        request.setShareStatement(true);
        return request;
    }
}
