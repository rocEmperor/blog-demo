package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description = "发送找回密码验证码请求")
public class ForgotPasswordSendRequest {
    @Schema(description = "注册邮箱", example = "zhangsan@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱为必填")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128)
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
