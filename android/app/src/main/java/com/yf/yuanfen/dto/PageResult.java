package com.yf.yuanfen.dto;

import java.util.List;

public class PageResult<T> {
    private long total;
    private int page;
    private int size;
    private List<T> items;

    public long getTotal() { return total; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public List<T> getItems() { return items; }
}
