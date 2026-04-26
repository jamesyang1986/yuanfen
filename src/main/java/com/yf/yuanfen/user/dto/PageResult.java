package com.yf.yuanfen.user.dto;

import java.util.List;

public class PageResult<T> {
    private long total;
    private int page;
    private int size;
    private List<T> items;

    public PageResult(long total, int page, int size, List<T> items) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.items = items;
    }

    public long getTotal() { return total; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public List<T> getItems() { return items; }
}
