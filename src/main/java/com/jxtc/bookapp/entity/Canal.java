package com.jxtc.bookapp.entity;

public class Canal {
    private Integer id;

    private String nounName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNounName() {
        return nounName;
    }

    public void setNounName(String nounName) {
        this.nounName = nounName == null ? null : nounName.trim();
    }
}