package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "分页数据")
public class PageData<T> {
    @Schema(description = "当前页数据")
    private List<T> list;
    @Schema(description = "总条数")
    private long total;
    @Schema(description = "当前页码（从 0 开始）")
    private int page;
    @Schema(description = "每页大小")
    private int size;

    public PageData() {
    }

    public PageData(List<T> list, long total, int page, int size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
