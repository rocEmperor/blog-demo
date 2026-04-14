package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "更新个人资料请求")
public class UpdateProfileRequest {
    @Schema(description = "昵称", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "昵称为必填")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{3,19}$", message = "昵称须 4-20 位，字母开头，仅字母、数字、下划线")
    private String nickname;

    @Schema(description = "个人简介", example = "专注 Java 与后端开发", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "个人简介为必填")
    @Size(min = 8, max = 50, message = "个人简介长度须为 8-50 字")
    private String bio;

    @Schema(description = "手机号（可为空）", example = "13800138000")
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "头像 URL（可为空）", example = "http://localhost:8080/api/files/user-logo/a.png")
    @Size(max = 512)
    private String avatarUrl;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
