package com.jxtc.bookapp.entity;

public class OrderCount {
    private double amount;
    private Integer countOrder;
    private String createTime;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Integer getCountOrder() {
        return countOrder;
    }

    public void setCountOrder(Integer countOrder) {
        this.countOrder = countOrder;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "OrderCount{" +
                "amount=" + amount +
                ", countOrder=" + countOrder +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
