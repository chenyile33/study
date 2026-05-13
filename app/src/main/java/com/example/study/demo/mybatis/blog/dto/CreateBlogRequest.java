package com.example.study.demo.mybatis.blog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBlogRequest {

    @NotBlank(message = "title不能为空")
    @Size(max = 255, message = "title长度不能超过255")
    private String title;

    private String content;

    @Size(max = 255, message = "description长度不能超过255")
    private String description;

    @Size(max = 255, message = "firstPicture长度不能超过255")
    private String firstPicture;

    @Size(max = 255, message = "flag长度不能超过255")
    private String flag;

    @NotNull(message = "appreciation不能为空")
    private Boolean appreciation;

    @NotNull(message = "commentabled不能为空")
    private Boolean commentabled;

    @NotNull(message = "published不能为空")
    private Boolean published;

    @NotNull(message = "recommend不能为空")
    private Boolean recommend;

    @NotNull(message = "shareStatement不能为空")
    private Boolean shareStatement;

    @Min(value = 0, message = "views不能小于0")
    private Integer views;

    private Long typeId;
}
