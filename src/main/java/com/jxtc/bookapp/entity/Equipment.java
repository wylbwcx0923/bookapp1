package com.jxtc.bookapp.entity;

import java.util.Date;

public class Equipment {
    private Integer id;

    private String equipmentId;

    private Integer canalId;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId == null ? null : equipmentId.trim();
    }

    public Integer getCanalId() {
        return canalId;
    }

    public void setCanalId(Integer canalId) {
        this.canalId = canalId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}