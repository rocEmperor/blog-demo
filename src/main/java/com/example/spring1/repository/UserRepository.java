package com.example.spring1.repository;

import com.example.spring1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    // 不用写任何方法！JpaRepository 已经包含了 findAll、save、deleteById 等所有基础操作
}
