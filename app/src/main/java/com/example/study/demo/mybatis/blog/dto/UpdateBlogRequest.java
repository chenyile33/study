package com.example.study.demo.mybatis.blog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateBlogRequest {

    @Size(max = 255, message = "title长度不能超过255")
    private String title;

    private String content;

    @Size(max = 255, message = "description长度不能超过255")
    private String description;

    @Size(max = 255, message = "firstPicture长度不能超过255")
    private String firstPicture;

    @Size(max = 255, message = "flag长度不能超过255")
    private String flag;

    private Boolean appreciation;

    private Boolean commentabled;

    private Boolean published;

    private Boolean recommend;

    private Boolean shareStatement;

    @Min(value = 0, message = "views不能小于0")
    private Integer views;

    private Long typeId;

    private Long userId;
}
