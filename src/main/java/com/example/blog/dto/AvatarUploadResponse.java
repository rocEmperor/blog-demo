package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "头像上传响应")
public class AvatarUploadResponse {
    @Schema(description = "头像可访问地址")
    private String url;
    public AvatarUploadResponse() {}
    public AvatarUploadResponse(String url) { this.url = url; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
