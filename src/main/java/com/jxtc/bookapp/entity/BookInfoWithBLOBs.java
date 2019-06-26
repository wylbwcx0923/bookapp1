package com.jxtc.bookapp.entity;

public class BookInfoWithBLOBs extends BookInfo {
    private String description;

    private String searchKeyField;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getSearchKeyField() {
        return searchKeyField;
    }

    public void setSearchKeyField(String searchKeyField) {
        this.searchKeyField = searchKeyField == null ? null : searchKeyField.trim();
    }
}