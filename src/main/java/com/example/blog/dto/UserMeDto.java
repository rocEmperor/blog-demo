package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "当前用户信息")
public class UserMeDto {
    @Schema(description = "用户 ID")
    private Integer userId;
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "头像 URL")
    private String avatarUrl;
    @Schema(description = "个人简介")
    private String bio;
    @Schema(description = "手机号")
    private String phone;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
