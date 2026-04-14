package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.*;
import com.example.blog.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "认证", description = "注册、登录、忘记密码、退出")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "注册", description = "成功后返回 userId")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "登录", description = "校验昵称密码后返回 JWT")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "退出")
    public Result<Void> logout() {
        return Result.success();
    }

    @PostMapping("/forgot-password/send-code")
    @Operation(summary = "忘记密码-发验证码")
    public Result<ForgotPasswordSendResponse> sendForgotCode(@Valid @RequestBody ForgotPasswordSendRequest request) {
        return Result.success(authService.sendForgotCode(request));
    }

    @PostMapping("/forgot-password/reset")
    @Operation(summary = "忘记密码-重置")
    public Result<Void> resetForgotPassword(@Valid @RequestBody ForgotPasswordResetRequest request) {
        authService.resetPassword(request);
        return Result.success();
    }
}
