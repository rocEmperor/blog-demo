package com.example.blog.repository;

import com.example.blog.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Integer> {
    Optional<PasswordResetCode> findTopByEmailOrderByCreatedAtDesc(String email);
    List<PasswordResetCode> findByEmailAndConsumedFalse(String email);
    Optional<PasswordResetCode> findTopByEmailAndConsumedFalseOrderByCreatedAtDesc(String email);
}
