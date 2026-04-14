package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {
    boolean existsByAuthor_IdAndTitle(Integer authorId, String title);
    boolean existsByAuthor_IdAndTitleAndIdNot(Integer authorId, String title, Integer id);
}
