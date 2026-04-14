package com.example.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "公开文章列表项")
public class PostPublicItemDto {
    @Schema(description = "文章 ID")
    private Integer id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "作者昵称")
    private String author;
    @Schema(description = "分类编码")
    private String categoryCode;
    @Schema(description = "摘要")
    private String excerpt;
    @Schema(description = "点赞数")
    private long likes;
    @Schema(description = "评论数")
    private long commentsCount;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updatedAt;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }
    public long getLikes() { return likes; }
    public void setLikes(long likes) { this.likes = likes; }
    public long getCommentsCount() { return commentsCount; }
    public void setCommentsCount(long commentsCount) { this.commentsCount = commentsCount; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
