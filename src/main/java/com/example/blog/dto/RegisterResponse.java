package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "注册响应")
public class RegisterResponse {
    @Schema(description = "用户 ID", example = "1001")
    private Integer userId;
    @Schema(description = "昵称", example = "zhangsan")
    private String nickname;

    public RegisterResponse() {}
    public RegisterResponse(Integer userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
