package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "点赞操作结果")
public class LikeResponse {
    @Schema(description = "当前点赞总数")
    private long likeCount;
    @Schema(description = "当前用户是否已点赞")
    private boolean liked;
    public LikeResponse() {}
    public LikeResponse(long likeCount, boolean liked) { this.likeCount = likeCount; this.liked = liked; }
    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }
}
