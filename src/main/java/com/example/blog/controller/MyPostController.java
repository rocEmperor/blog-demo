package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.*;
import com.example.blog.security.JwtUserPrincipal;
import com.example.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Tag(name = "我的文章", description = "当前登录用户文章管理")
@RequestMapping("/users/me/posts")
public class MyPostController {
    @Autowired private PostService postService;

    @GetMapping
    @Operation(summary = "我的文章列表")
    public Result<PageData<MyPostRowDto>> listMine(@AuthenticationPrincipal JwtUserPrincipal principal,
                                                   @RequestParam(required = false) String q,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        if (size > 100) size = 100;
        return Result.success(postService.listMine(principal.getId(), q, page, size));
    }

    @PostMapping
    @Operation(summary = "新建文章")
    public Result<PostCreateResponse> create(@AuthenticationPrincipal JwtUserPrincipal principal,
                                             @Valid @RequestBody UpsertPostRequest request) {
        return Result.success(postService.create(principal.getId(), request));
    }

    @PutMapping("/{id:\\d+}")
    @Operation(summary = "编辑文章")
    public Result<PostDetailDto> update(@AuthenticationPrincipal JwtUserPrincipal principal,
                                        @PathVariable Integer id,
                                        @Valid @RequestBody UpsertPostRequest request) {
        return Result.success(postService.update(principal.getId(), id, request));
    }

    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "删除我的文章")
    public Result<Void> delete(@AuthenticationPrincipal JwtUserPrincipal principal,
                               @PathVariable Integer id) {
        postService.delete(principal.getId(), id);
        return Result.success();
    }
}
