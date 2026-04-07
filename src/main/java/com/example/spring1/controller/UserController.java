package com.example.spring1.controller;

import com.example.spring1.common.Result;
import com.example.spring1.entity.User;
import com.example.spring1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import com.example.spring1.dto.UserAddDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Date;
import java.util.List;
@Tag(name = "用户管理", description = "用户相关的增删改查接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    // 查询所有用户
    @Operation(summary = "查询所有用户", description = "查询所有用户信息")
    @GetMapping("/list")
    public  Result<List<User>> list () {
        List<User> list = userService.findAll();
        return Result.success(list);
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
