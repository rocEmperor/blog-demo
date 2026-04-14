package com.example.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Schema(description = "评论项")
public class CommentDto {
    @Schema(description = "评论 ID")
    private Integer id;
    @Schema(description = "评论作者")
    private String author;
    @Schema(description = "作者头像 URL")
    private String avatar;
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "当前用户是否可删除")
    private boolean canDelete;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date time;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isCanDelete() { return canDelete; }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
}
