package com.jxtc.bookapp.entity;

public class CanalPopularizeCount {
    private Integer id;

    private String oneDay;

    private Integer nounId;

    private Integer registerCount;

    private Integer downCount;

    private Integer payCount;

    private Double payMoney;

    private Double cost;

    private String remark;

    private String canalName;

    private String downOrderProportion;//下单比例

    private Double recoverMoney;//回本金额

    private String recoverProportion;//回本比例

    public Integer getRegisterCount() {
        return registerCount;
    }

    public void setRegisterCount(Integer registerCount) {
        this.registerCount = registerCount;
    }

    public String getCanalName() {
        return canalName;
    }

    public void setCanalName(String canalName) {
        this.canalName = canalName;
    }

    public String getDownOrderProportion() {
        return downOrderProportion;
    }

    public void setDownOrderProportion(String downOrderProportion) {
        this.downOrderProportion = downOrderProportion;
    }

    public Double getRecoverMoney() {
        return recoverMoney;
    }

    public void setRecoverMoney(Double recoverMoney) {
        this.recoverMoney = recoverMoney;
    }

    public String getRecoverProportion() {
        return recoverProportion;
    }

    public void setRecoverProportion(String recoverProportion) {
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