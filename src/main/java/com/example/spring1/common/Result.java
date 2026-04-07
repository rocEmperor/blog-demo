package com.example.spring1.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;
    // 成功
    public static <T> Result<T> success (T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("成功");
        r.setData(data);
        return r;
    }
    public static <T> Result<T> success(String msg) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(null); // 空数据
        return r;
    }
    // 成功-无数据
    public static <T> Result<T> success() {
        return success(null);
    }
    // 失败
    public static <T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }
}
