package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "注册请求")
public class RegisterRequest {
    @Schema(description = "昵称（4-20 位，字母开头）", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "昵称为必填")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{3,19}$", message = "昵称须 4-20 位，字母开头，仅字母、数字、下划线")
    private String nickname;

    @Schema(description = "邮箱", example = "zhangsan@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱为必填")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128)
    private String email;

    @Schema(description = "密码（8-20 位，字母+数字）", example = "abc12345", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码为必填")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = "密码须 8-20 位且同时包含字母与数字")
    private String password;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
