package com.example.common.core.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Common page response wrapper.
 */
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> records;
    private final long total;
    private final long pageNum;
    private final long pageSize;
    private final long pages;

    private PageResult(List<T> records, long total, long pageNum, long pageSize) {
        this.records = immutableRecords(records);
        this.total = Math.max(total, 0);
        this.pageNum = Math.max(pageNum, 1);
        this.pageSize = Math.max(pageSize, 1);
        this.pages = calculatePages(this.total, this.pageSize);
    }

    public static <T> PageResult<T> of(List<T> records, long total, PageParam pageParam) {
        PageParam normalizedPageParam = pageParam == null ? new PageParam() : pageParam;
        return of(records, total, normalizedPageParam.getPageNum(), normalizedPageParam.getPageSize());
    }

    public static <T> PageResult<T> of(List<T> records, long total, long pageNum, long pageSize) {
        return new PageResult<>(records, total, pageNum, pageSize);
    }

    public static <T> PageResult<T> empty(PageParam pageParam) {
        return of(Collections.emptyList(), 0, pageParam);
    }

    public static <T> PageResult<T> empty(long pageNum, long pageSize) {
        return of(Collections.emptyList(), 0, pageNum, pageSize);
    }

    /**
     * Whether the current page has a previous page based on normalized pageNum.
     */
    public boolean hasPrevious() {
        return pageNum > 1;
    }

    /**
     * Whether the current page has a next page based on total pages.
     */
    public boolean hasNext() {
        return pageNum < pages;
    }

    public List<T> getRecords() {
        return records;
    }

    public long getTotal() {
        return total;
    }

    public long getPageNum() {
        return pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public long getPages() {
        return pages;
    }

    private static long calculatePages(long total, long pageSize) {
        if (total == 0) {
            return 0;
        }
        return (total + pageSize - 1) / pageSize;
    }

    private static <T> List<T> immutableRecords(List<T> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(records));
    }
}
