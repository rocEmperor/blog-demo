package com.example.spring1.controller;

import com.example.spring1.common.Result;
import com.example.spring1.dto.AuthResponse;
import com.example.spring1.dto.LoginRequest;
import com.example.spring1.dto.UserAddDTO;
import com.example.spring1.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "认证", description = "注册、登录与 JWT 签发；其它接口需在 Header 携带 Bearer Token（详见 doc/JWT认证实现说明.md）")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "注册", description = "创建用户并返回 JWT")
    public Result<AuthResponse> register(@Valid @RequestBody UserAddDTO dto) {
        AuthResponse body = authService.register(dto.getUsername(), dto.getPassword());
        return Result.success(body);
    }

    @PostMapping("/login")
    @Operation(summary = "登录", description = "校验用户名密码后返回 JWT")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse body = authService.login(request);
        return Result.success(body);
    }
}
