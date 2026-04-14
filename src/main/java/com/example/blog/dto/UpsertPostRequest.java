package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "创建/编辑文章请求")
public class UpsertPostRequest {
    @Schema(description = "标题", example = "Spring Boot 入门", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标题为必填")
    @Size(min = 4, max = 30, message = "标题长度须为 4-30 字")
    private String title;
    @Schema(description = "可见范围：open 或 only_myself", example = "open", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "可见范围为必填")
    @Pattern(regexp = "^(open|only_myself)$", message = "可见范围须为 open 或 only_myself")
    private String visibility;
    @Schema(description = "分类编码（1-7）", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类为必填")
    @Pattern(regexp = "^[1-7]$", message = "分类代码须为 1-7")
    private String categoryCode;
    @Schema(description = "正文（HTML）", example = "<p>hello</p>", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "正文为必填")
    private String body;
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
