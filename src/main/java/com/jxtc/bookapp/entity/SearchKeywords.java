package com.jxtc.bookapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class SearchKeywords {
    private Integer id;

    private String keyWords;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date searchTime;

    private int secrchCount;

    public int getSecrchCount() {
        return secrchCount;
    }

    public void setSecrchCount(int secrchCount) {
        this.secrchCount = secrchCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords == null ? null : keyWords.trim();
    }

    public Date getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Date searchTime) {
        this.searchTime = searchTime;
    }
}