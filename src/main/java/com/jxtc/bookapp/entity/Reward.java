package com.jxtc.bookapp.entity;

import java.util.Date;

public class Reward {
    private Integer id;

    private String userId;

    private Integer bookId;

    private Integer type;

    private Integer mun;

    private Integer amount;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMun() {
        return mun;
    }

    public void setMun(Integer mun) {
        this.mun = mun;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", bookId=" + bookId +
                ", type=" + type +
                ", mun=" + mun +
                ", amount=" + amount +
                ", createTime=" + createTime +
                '}';
    }
}