package com.example.blog.service;

import com.example.blog.common.BusinessException;
import com.example.blog.dto.*;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.PostLike;
import com.example.blog.entity.User;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostLikeRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.util.PostTextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired private PostRepository postRepository;
    @Autowired private UserService userService;
    @Autowired private CommentRepository commentRepository;
    @Autowired private PostLikeRepository postLikeRepository;

    @Transactional(readOnly = true)
    public PageData<PostPublicItemDto> listPublic(String q, String categoryCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Post> result = postRepository.findAll(buildPublicSpec(q, categoryCode), pageable);
        List<PostPublicItemDto> list = result.getContent().stream().map(this::toPublicItem).collect(Collectors.toList());
        return new PageData<>(list, result.getTotalElements(), page, size);
    }

    private Specification<Post> buildPublicSpec(String q, String categoryCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Post, User> author = root.join("author", JoinType.INNER);
            predicates.add(cb.equal(root.get("visibility"), "open"));
            predicates.add(cb.isNull(author.get("deletedAt")));
            String qq = q != null ? q.trim() : "";
            if (!qq.isEmpty()) predicates.add(cb.like(cb.lower(root.get("title")), "%" + qq.toLowerCase() + "%"));
            String cat = categoryCode != null ? categoryCode.trim() : "";
            if (!cat.isEmpty()) predicates.add(cb.equal(root.get("categoryCode"), cat));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional(readOnly = true)
    public PostDetailDto getDetail(Integer postId, Integer viewerUserId) {
        Post p = postRepository.findById(postId).orElseThrow(() -> new BusinessException(404, "文章不存在"));
        User author = p.getAuthor();
        if (author.getDeletedAt() != null) throw new BusinessException(404, "文章不存在");
        if ("only_myself".equals(p.getVisibility()) && (viewerUserId == null || !viewerUserId.equals(author.getId()))) {
            throw new BusinessException(403, "无权查看该文章");
        }
        return toDetail(p, viewerUserId);
    }

    @Transactional(readOnly = true)
    public PageData<MyPostRowDto> listMine(Integer userId, String q, int page, int size) {
        userService.requireActiveById(userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Specification<Post> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Post, User> author = root.join("author", JoinType.INNER);
            predicates.add(cb.equal(author.get("id"), userId));
            String qq = q != null ? q.trim() : "";
            if (!qq.isEmpty()) predicates.add(cb.like(root.get("title"), "%" + qq + "%"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Post> result = postRepository.findAll(spec, pageable);
        List<MyPostRowDto> list = result.getContent().stream().map(this::toMyRow).collect(Collectors.toList());
        return new PageData<>(list, result.getTotalElements(), page, size);
    }

    @Transactional
    public PostCreateResponse create(Integer userId, UpsertPostRequest request) {
        validateBody(request.getBody());
        User author = userService.requireActiveById(userId);
        String titleTrim = request.getTitle().trim();
        if (postRepository.existsByAuthor_IdAndTitle(author.getId(), titleTrim)) throw new BusinessException(409, "您已有同名文章");
        Post p = new Post();
        p.setAuthor(author);
        p.setTitle(titleTrim);
        p.setVisibility(request.getVisibility());
        p.setCategoryCode(request.getCategoryCode());
        p.setBody(request.getBody().trim());
        p.setLikeCount(0);
        p.setCommentCount(0);
        Post saved = postRepository.save(p);
        return new PostCreateResponse(saved.getId());
    }

    @Transactional
    public PostDetailDto update(Integer userId, Integer postId, UpsertPostRequest request) {
        validateBody(request.getBody());
        Post p = postRepository.findById(postId).orElseThrow(() -> new BusinessException(404, "文章不存在"));
        if (!p.getAuthor().getId().equals(userId)) throw new BusinessException(403, "无权修改该文章");
        String titleTrim = request.getTitle().trim();
        if (postRepository.existsByAuthor_IdAndTitleAndIdNot(userId, titleTrim, postId)) {
            throw new BusinessException(409, "您已有其它文章使用该标题");
        }
        p.setTitle(titleTrim);
        p.setVisibility(request.getVisibility());
        p.setCategoryCode(request.getCategoryCode());
        p.setBody(request.getBody().trim());
        postRepository.save(p);
        return toDetail(p, userId);
    }

    @Transactional
    public void delete(Integer userId, Integer postId) {
        Post p = postRepository.findById(postId).orElseThrow(() -> new BusinessException(404, "文章不存在"));
        if (!p.getAuthor().getId().equals(userId)) throw new BusinessException(403, "无权删除该文章");
        postRepository.delete(p);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> listComments(Integer postId, Integer viewerUserId) {
        getDetail(postId, viewerUserId);
        return commentRepository.findByPost_IdOrderByCreatedAtDesc(postId).stream()
                .map(c -> toCommentDto(c, viewerUserId)).collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Integer postId, Integer userId, CommentCreateRequest request) {
        User user = userService.requireActiveById(userId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(404, "文章不存在"));
        getDetail(postId, userId);
        Comment c = new Comment();
        c.setPost(post);
        c.setUser(user);
        c.setContent(request.getContent().trim());
        Comment saved = commentRepository.save(c);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        return toCommentDto(saved, userId);
    }

    @Transactional
    public void deleteMyComment(Integer userId, Integer commentId) {
        Comment c = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(404, "评论不存在"));
        if (!c.getUser().getId().equals(userId)) throw new BusinessException(403, "无权删除该评论");
        Post post = c.getPost();
        commentRepository.delete(c);
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);
    }

    @Transactional
    public LikeResponse like(Integer postId, Integer userId) {
        getDetail(postId, userId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(404, "文章不存在"));
        if (!postLikeRepository.existsByPost_IdAndUser_Id(postId, userId)) {
            PostLike row = new PostLike();
            row.setPost(post);
            row.setUser(userService.requireActiveById(userId));
            postLikeRepository.save(row);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
        }
        return new LikeResponse(post.getLikeCount(), true);
    }

    @Transactional
    public LikeResponse unlike(Integer postId, Integer userId) {
        getDetail(postId, userId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(404, "文章不存在"));
        if (postLikeRepository.existsByPost_IdAndUser_Id(postId, userId)) {
            postLikeRepository.deleteByPost_IdAndUser_Id(postId, userId);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
        }
        return new LikeResponse(post.getLikeCount(), false);
    }

    private void validateBody(String body) {
        if (PostTextUtils.plainTextLength(body) > 5000) throw new BusinessException(400, "正文纯文本长度不能超过 5000 字");
    }

    private PostPublicItemDto toPublicItem(Post p) {
        PostPublicItemDto dto = new PostPublicItemDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setAuthor(p.getAuthor().getUsername());
        dto.setCategoryCode(p.getCategoryCode());
        dto.setExcerpt(PostTextUtils.excerpt(p.getBody(), 120));
        dto.setLikes(p.getLikeCount());
        dto.setCommentsCount(p.getCommentCount());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }

    private MyPostRowDto toMyRow(Post p) {
        MyPostRowDto dto = new MyPostRowDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setStatusLabel("open".equals(p.getVisibility()) ? "公开" : "仅自己可见");
        dto.setCategoryCode(p.getCategoryCode());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }

    private PostDetailDto toDetail(Post p, Integer viewerUserId) {
        PostDetailDto dto = new PostDetailDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setAuthor(p.getAuthor().getUsername());
        dto.setCategoryCode(p.getCategoryCode());
        dto.setVisibility(p.getVisibility());
        dto.setBody(p.getBody());
        dto.setLikes(p.getLikeCount());
        dto.setCommentsCount(p.getCommentCount());
        dto.setLikedByMe(viewerUserId != null && postLikeRepository.existsByPost_IdAndUser_Id(p.getId(), viewerUserId));
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }

    private CommentDto toCommentDto(Comment c, Integer viewerUserId) {
        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setAuthor(c.getUser().getUsername());
        dto.setAvatar(c.getUser().getAvatarUrl() != null ? c.getUser().getAvatarUrl() : "");
        dto.setContent(c.getContent());
        dto.setTime(c.getCreatedAt());
        dto.setCanDelete(viewerUserId != null && viewerUserId.equals(c.getUser().getId()));
        return dto;
    }
}
