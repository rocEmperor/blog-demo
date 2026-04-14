package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import com.example.blog.dto.UserAddDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
@Tag(name = "用户管理", description = "用户相关的增删改查接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Autowired
    private UserService userService;
    // 查询所有用户
    @Operation(summary = "查询所有用户", description = "查询所有用户信息")
    @GetMapping("/list")
    public  Result<List<User>> list (HttpServletRequest request) {
        List<User> list = userService.findAll();
        Result<List<User>> result = Result.success(list);
        try {
            String resultJson = OBJECT_MAPPER.writeValueAsString(result);
            log.info("接口调用完成 | method={} | uri={} | query={} | remoteIp={} | response={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    request.getRemoteAddr(),
                    resultJson);
        } catch (JsonProcessingException e) {
            log.warn("接口调用完成(序列化失败) | method={} | uri={} | query={} | remoteIp={} | response={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    request.getRemoteAddr(),
                    result,
                    e);
        }
        return result;
    }
    // 根据ID查单个用户
    @Operation(summary = "根据ID查询用户", description = "通过用户ID获取单个用户的完整信息")
    @GetMapping("/getById")
    public  Result<User> getById(@RequestParam Integer id) {
        User user = userService.getById(id);
        return Result.success(user);
    }
    // 新增用户
    @PostMapping("/add")
    @Operation(summary = "新增用户", description = "创建新用户，带参数校验：用户名2-20位，密码不少于6位")
    public  Result<User> add(@Valid @RequestBody UserAddDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setCreateTime(new Date());
        User saved = userService.add(user);
        return Result.success(saved);
    }

    // 根据ID删除单个用户
    @GetMapping("/deleteById")
    @Operation(summary = "根据ID删除单个用户", description = "根据ID删除单个用户")
    public Result deleteById(@RequestParam Integer id) {
        userService.deleteById(id);
        return Result.success("删除成功");
    }
}
