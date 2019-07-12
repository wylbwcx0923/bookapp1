package com.jxtc.bookapp.entity;

public class UserInfoKey {
    private Integer id;

    private String userId;

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

    @Override
    public String toString() {
        return "UserInfoKey{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                '}';
    }
}