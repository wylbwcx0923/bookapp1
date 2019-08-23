package com.jxtc.bookapp.entity;


import java.util.Date;

public class BangdanBooks {
    private Integer id;

    private Integer bangdanId;

    private Integer bookId;

    private Integer sort;

    private Date createTime;

    private Date updateTime;

    private BookInfo bookInfo;


    public BookInfo getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBangdanId() {
        return bangdanId;
    }

    public void setBangdanId(Integer bangdanId) {
        this.bangdanId = bangdanId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}