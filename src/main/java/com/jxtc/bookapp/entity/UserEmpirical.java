package com.jxtc.bookapp.entity;

import java.util.Date;

public class UserEmpirical extends UserEmpiricalKey {
    private Integer empirical;

    private Integer grade;

    private Double userDiscount;

    private Date createTime;

    private Date updateTime;

    public Integer getEmpirical() {
        return empirical;
    }

    public void setEmpirical(Integer empirical) {
        this.empirical = empirical;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Double getUserDiscount() {
        return userDiscount;
    }

    public void setUserDiscount(Double userDiscount) {
        this.userDiscount = userDiscount;
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