package com.example.study.demo.blog.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_blog")
public class Blog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("appreciation")
    private Boolean appreciation;

    @TableField("commentabled")
    private Boolean commentabled;

    @TableField("content")
    private String content;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("description")
    private String description;

    @TableField("first_picture")
    private String firstPicture;

    @TableField("flag")
    private String flag;

    @TableField("published")
    private Boolean published;

    @TableField("recommend")
    private Boolean recommend;

    @TableField("share_statement")
    private Boolean shareStatement;

    @TableField("title")
    private String title;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("views")
    private Integer views;

    @TableField("type_id")
    private Long typeId;

    @TableField("user_id")
    private Long userId;
}
