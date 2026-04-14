package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "创建文章响应")
public class PostCreateResponse {
    @Schema(description = "新建文章 ID")
    private Integer id;
    public PostCreateResponse() {}
    public PostCreateResponse(Integer id) { this.id = id; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}
