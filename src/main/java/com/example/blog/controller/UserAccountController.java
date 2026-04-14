package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.*;
import com.example.blog.security.JwtUserPrincipal;
import com.example.blog.service.FileStorageService;
import com.example.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Tag(name = "用户账号", description = "当前登录用户资料、安全设置、头像与注销")
@RequestMapping("/users")
public class UserAccountController {
    @Autowired
    private UserService userService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/me")
    @Operation(summary = "获取当前用户资料")
    public Result<UserMeDto> me(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return Result.success(userService.toMeDto(userService.requireActiveById(principal.getId())));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "更新资料与头像地址", description = "支持昵称、简介、手机号、头像 URL")
    public Result<UserMeDto> updateProfile(@AuthenticationPrincipal JwtUserPrincipal principal,
                                           @Valid @RequestBody UpdateProfileRequest request) {
        return Result.success(userService.updateProfile(principal.getId(), request));
    }

    @PutMapping("/me/security")
    @Operation(summary = "更新邮箱与密码")
    public Result<Void> updateSecurity(@AuthenticationPrincipal JwtUserPrincipal principal,
                                       @Valid @RequestBody UpdateSecurityRequest request) {
        userService.updateSecurity(principal.getId(), request);
        return Result.success();
    }

    @DeleteMapping("/me")
    @Operation(summary = "注销账号", description = "执行软删除，不物理删除用户行")
    public Result<Void> delete(@AuthenticationPrincipal JwtUserPrincipal principal,
                               @RequestBody(required = false) @Valid DeleteAccountRequest request) {
        userService.softDeleteAccount(principal.getId(), request);
        return Result.success();
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传头像", description = "上传成功后返回可访问的头像 URL")
    public Result<AvatarUploadResponse> uploadAvatar(@AuthenticationPrincipal JwtUserPrincipal principal,
                                                     @RequestPart("file") MultipartFile file,
                                                     HttpServletRequest request) {
        userService.requireActiveById(principal.getId());
        String filename = fileStorageService.storeAvatar(file);
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        String ctx = request.getContextPath();
        boolean defaultPort = ("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443);
        String portPart = defaultPort ? "" : ":" + port;
        return Result.success(new AvatarUploadResponse(scheme + "://" + host + portPart + ctx + "/files/user-logo/" + filename));
    }
}
