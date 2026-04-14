package com.example.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello () {
        return "Hello Spring Boot! 我成功跑起来啦！哈哈哈";
    }
}
