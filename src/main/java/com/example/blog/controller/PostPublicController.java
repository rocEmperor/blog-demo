package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.*;
import com.example.blog.security.JwtUserPrincipal;
import com.example.blog.service.JwtService;
import com.example.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "公开文章", description = "公开文章列表、详情、评论、点赞")
@RequestMapping("/posts")
public class PostPublicController {
    @Autowired private PostService postService;
    @Autowired private JwtService jwtService;

    @GetMapping("/public")
    @Operation(summary = "获取公开文章列表")
    public Result<PageData<PostPublicItemDto>> listPublic(@RequestParam(required = false) String categoryCode,
                                                          @Parameter(description = "标题模糊搜索关键字") @RequestParam(required = false) String q,
                                                          @Parameter(description = "页码，从 0 开始") @RequestParam(defaultValue = "0") int page,
                                                          @Parameter(description = "每页数量，最大 100") @RequestParam(defaultValue = "10") int size) {
        if (size > 100) size = 100;
        return Result.success(postService.listPublic(q, categoryCode, page, size));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "获取文章详情")
    public Result<PostDetailDto> detail(@PathVariable Integer id, HttpServletRequest request) {
        Optional<Integer> viewerId = jwtService.tryParseBearerUserId(request);
        return Result.success(postService.getDetail(id, viewerId.orElse(null)));
    }

    @GetMapping("/{postId:\\d+}/comments")
    @Operation(summary = "获取评论列表")
    public Result<List<CommentDto>> comments(@PathVariable Integer postId, HttpServletRequest request) {
        Optional<Integer> viewerId = jwtService.tryParseBearerUserId(request);
        return Result.success(postService.listComments(postId, viewerId.orElse(null)));
    }

    @PostMapping("/{postId:\\d+}/comments")
    @Operation(summary = "发表评论")
    public Result<CommentDto> addComment(@AuthenticationPrincipal JwtUserPrincipal principal,
                                         @PathVariable Integer postId,
                                         @Valid @RequestBody CommentCreateRequest request) {
        return Result.success(postService.addComment(postId, principal.getId(), request));
    }

    @PostMapping("/{postId:\\d+}/like")
    @Operation(summary = "点赞文章")
    public Result<LikeResponse> like(@AuthenticationPrincipal JwtUserPrincipal principal,
                                     @PathVariable Integer postId) {
        return Result.success(postService.like(postId, principal.getId()));
    }

    @DeleteMapping("/{postId:\\d+}/like")
    @Operation(summary = "取消点赞")
    public Result<LikeResponse> unlike(@AuthenticationPrincipal JwtUserPrincipal principal,
                                       @PathVariable Integer postId) {
        return Result.success(postService.unlike(postId, principal.getId()));
    }
}
