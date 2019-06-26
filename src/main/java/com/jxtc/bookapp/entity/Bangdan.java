package com.jxtc.bookapp.entity;

public class Bangdan {
    private Integer id;

    private Integer bangdanId;

    private String bangdanName;

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

    public String getBangdanName() {
        return bangdanName;
    }

    public void setBangdanName(String bangdanName) {
        this.bangdanName = bangdanName == null ? null : bangdanName.trim();
    }
}