package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description = "发表评论请求")
public class CommentCreateRequest {
    @Schema(description = "评论内容", example = "写得很好", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "评论内容为必填")
    @Size(min = 1, max = 300, message = "评论长度须为 1-300 字")
    private String content;
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
