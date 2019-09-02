package com.jxtc.bookapp.entity;

public class CanalPopularizeCount {
    private Integer id;

    private String oneDay;

    private Integer nounId;

    private Integer downCount;

    private Integer payCount;

    private Double payMoney;

    private Double cost;

    private String remark;

    private String canalName;

    private double downOrderProportion;//下单比例

    private double recoverMoney;//回本金额

    private double recoverProportion;//回本比例

    public String getCanalName() {
        return canalName;
    }

    public void setCanalName(String canalName) {
        this.canalName = canalName;
    }

    public double getDownOrderProportion() {
        return downOrderProportion;
    }

    public void setDownOrderProportion(double downOrderProportion) {
        this.downOrderProportion = downOrderProportion;
    }

    public double getRecoverMoney() {
        return recoverMoney;
    }

    public void setRecoverMoney(double recoverMoney) {
        this.recoverMoney = recoverMoney;
    }

    public double getRecoverProportion() {
        return recoverProportion;
    }

    public void setRecoverProportion(double recoverProportion) {
        this.recoverProportion = recoverProportion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOneDay() {
        return oneDay;
    }

    public void setOneDay(String oneDay) {
        this.oneDay = oneDay == null ? null : oneDay.trim();
    }

    public Integer getNounId() {
        return nounId;
    }

    public void setNounId(Integer nounId) {
        this.nounId = nounId;
    }

    public Integer getDownCount() {
        return downCount;
    }

    public void setDownCount(Integer downCount) {
        this.downCount = downCount;
    }

    public Integer getPayCount() {
        return payCount;
    }

    public void setPayCount(Integer payCount) {
        this.payCount = payCount;
    }

    public Double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Double payMoney) {
        this.payMoney = payMoney;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}