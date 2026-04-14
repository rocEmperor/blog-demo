package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "更新安全信息请求")
public class UpdateSecurityRequest {
    @Schema(description = "新邮箱", example = "new@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱为必填")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128)
    private String email;

    @Schema(description = "当前密码", example = "abc12345", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "当前密码为必填")
    private String currentPassword;

    @Schema(description = "新密码", example = "new12345", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码为必填")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = "新密码须 8-20 位且同时包含字母与数字")
    private String newPassword;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
