package com.example.study.demo.blog.dto;

import com.example.study.demo.blog.domain.Blog;

import java.time.LocalDateTime;

public class BlogResponse {

    private final Long id;
    private final Boolean appreciation;
    private final Boolean commentabled;
    private final String content;
    private final LocalDateTime createTime;
    private final String description;
    private final String firstPicture;
    private final String flag;
    private final Boolean published;
    private final Boolean recommend;
    private final Boolean shareStatement;
    private final String title;
    private final LocalDateTime updateTime;
    private final Integer views;
    private final Long typeId;
    private final Long userId;

    private BlogResponse(Blog blog) {
        this.id = blog.getId();
        this.appreciation = blog.getAppreciation();
        this.commentabled = blog.getCommentabled();
        this.content = blog.getContent();
        this.createTime = blog.getCreateTime();
        this.description = blog.getDescription();
        this.firstPicture = blog.getFirstPicture();
        this.flag = blog.getFlag();
        this.published = blog.getPublished();
        this.recommend = blog.getRecommend();
        this.shareStatement = blog.getShareStatement();
        this.title = blog.getTitle();
        this.updateTime = blog.getUpdateTime();
        this.views = blog.getViews();
        this.typeId = blog.getTypeId();
        this.userId = blog.getUserId();
    }

    public static BlogResponse from(Blog blog) {
        return new BlogResponse(blog);
    }

    public Long getId() {
        return id;
    }

    public Boolean getAppreciation() {
        return appreciation;
    }

    public Boolean getCommentabled() {
        return commentabled;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public String getDescription() {
        return description;
    }

    public String getFirstPicture() {
        return firstPicture;
    }

    public String getFlag() {
        return flag;
    }

    public Boolean getPublished() {
        return published;
    }

    public Boolean getRecommend() {
        return recommend;
    }

    public Boolean getShareStatement() {
        return shareStatement;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public Integer getViews() {
        return views;
    }

    public Long getTypeId() {
        return typeId;
    }

    public Long getUserId() {
        return userId;
    }
}
