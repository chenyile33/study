package com.example.common.core.page;

import java.io.Serializable;

/**
 * Common page request parameters.
 */
public class PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    private int pageNum;
    private int pageSize;

    public PageParam() {
        this(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE);
    }

    public PageParam(int pageNum, int pageSize) {
        this.pageNum = normalizePageNum(pageNum);
        this.pageSize = normalizePageSize(pageSize);
    }

    public static PageParam of(int pageNum, int pageSize) {
        return new PageParam(pageNum, pageSize);
    }

    /**
     * Offset starts from 0 and can be used by SQL limit queries.
     */
    public long offset() {
        return (long) (pageNum - 1) * pageSize;
    }

    public int limit() {
        return pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = normalizePageNum(pageNum);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = normalizePageSize(pageSize);
    }

    private static int normalizePageNum(int pageNum) {
        return pageNum < 1 ? DEFAULT_PAGE_NUM : pageNum;
    }

    /**
     * Keep page size in a bounded range to avoid accidental large queries.
     */
    private static int normalizePageSize(int pageSize) {
        if (pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
