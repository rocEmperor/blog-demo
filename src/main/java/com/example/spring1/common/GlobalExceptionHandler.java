package com.example.spring1.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 所有异常都走这里
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        e.printStackTrace();
        return Result.error(500, "服务器异常：" + e.getMessage());
    }
    // 空指针单独处理
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointer(NullPointerException e) {
        return Result.error(400, "数据不存在或为空");
    }
    // 参数校验异常处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        // 获取第一条校验失败的提示信息
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(400, msg);
    }
}
