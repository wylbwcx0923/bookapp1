package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Manager;
import com.jxtc.bookapp.utils.PageResult;

import java.util.List;

public interface ManagerService {
    /**
     * 管理后台管理员注册
     *
     * @param manager
     */
    void register(Manager manager);

    /**
     * 管理后台管理员登录
     *
     * @param username
     * @param password
     * @return
     */
    Manager login(String username, String password);

    /**
     * 判断用户名是否存在
     *
     * @param username
     * @return
     */
    boolean isExistUser(String username);

    /**
     * 获得列表
     *
     * @return
     */
    List<Manager> getList();

    /**
     * 通过id删除
     *
     * @param id
     */
    void delManagerById(int id);
}
