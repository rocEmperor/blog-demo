package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "发送验证码响应")
public class ForgotPasswordSendResponse {
    @Schema(description = "下次允许发送的冷却秒数", example = "60")
    private int cooldownSec;
    public ForgotPasswordSendResponse() {}
    public ForgotPasswordSendResponse(int cooldownSec) { this.cooldownSec = cooldownSec; }
    public int getCooldownSec() { return cooldownSec; }
    public void setCooldownSec(int cooldownSec) { this.cooldownSec = cooldownSec; }
}
