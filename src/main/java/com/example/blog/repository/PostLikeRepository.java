package com.example.blog.repository;

import com.example.blog.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    boolean existsByPost_IdAndUser_Id(Integer postId, Integer userId);
    void deleteByPost_IdAndUser_Id(Integer postId, Integer userId);
}
