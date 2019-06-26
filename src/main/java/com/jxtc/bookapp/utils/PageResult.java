package com.jxtc.bookapp.utils;

import java.util.List;

/**
 * 本类是用于分页操作的工具类
 *
 * @param <T> 分页内容的泛型
 * @author wyl
 */
public class PageResult<T> {
    private int pageIndex;//当前第几页
    private int pageSize;//每页显示多少条数据
    private int total;//总共多少条数据
    private int pageCounts;//总共多少页
    private List<T> pageList;//页面具体内容
    private Object param;//备用参数


    public PageResult() {
    }

    public PageResult(int pageIndex, int total, List<T> pageList, int pageSize) {
        this.pageIndex = pageIndex;
        this.total = total;
        this.pageList = pageList;
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageCounts() {
        return total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
    }

    public void setPageCounts(int pageCounts) {
        this.pageCounts = pageCounts;
    }

    public List<T> getPageList() {
        return pageList;
    }

    public void setPageList(List<T> pageList) {
        this.pageList = pageList;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }
}
