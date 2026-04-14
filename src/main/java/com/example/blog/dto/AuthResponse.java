package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登录/注册成功返回（含 JWT）")
public class AuthResponse {

    @Schema(description = "JWT")
    private String token;

    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "用户 ID")
    private Integer userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    public AuthResponse() {
    }

    public AuthResponse(String token, String tokenType, Integer userId, String nickname, String email) {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
