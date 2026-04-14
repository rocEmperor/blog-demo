package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.security.JwtUserPrincipal;
import com.example.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "评论管理", description = "当前登录用户评论管理")
@RequestMapping("/users/me/comments")
public class CommentManageController {
    @Autowired private PostService postService;

    @DeleteMapping("/{commentId:\\d+}")
    @Operation(summary = "删除我的评论")
    public Result<Void> deleteMine(@AuthenticationPrincipal JwtUserPrincipal principal,
                                   @PathVariable Integer commentId) {
        postService.deleteMyComment(principal.getId(), commentId);
        return Result.success();
    }
}
