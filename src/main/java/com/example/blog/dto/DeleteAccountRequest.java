package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;

@Schema(description = "注销账号请求")
public class DeleteAccountRequest {
    @Schema(description = "确认邮箱（可选，填写时需与当前账号邮箱一致）", example = "zhangsan@test.com")
    @Size(max = 128)
    private String confirmEmail;
    public String getConfirmEmail() { return confirmEmail; }
    public void setConfirmEmail(String confirmEmail) { this.confirmEmail = confirmEmail; }
}
