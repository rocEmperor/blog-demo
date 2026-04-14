package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "找回密码重置请求")
public class ForgotPasswordResetRequest {
    @Schema(description = "注册邮箱", example = "zhangsan@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱为必填")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128)
    private String email;

    @Schema(description = "验证码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "验证码为必填")
    private String code;

    @Schema(description = "新密码", example = "new12345", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码为必填")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = "新密码须 8-20 位且同时包含字母与数字")
    private String newPassword;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
