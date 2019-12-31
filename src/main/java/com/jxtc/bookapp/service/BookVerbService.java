package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.BookVerb;

import java.util.Map;

/**
 * @author 不忘初心
 * 图书推广服务接口
 */
public interface BookVerbService {
    /**
     * 添加图书推广
     *
     * @param bookVerb
     */
    void addBookVerb(BookVerb bookVerb);

    /**
     * 修改图书推广
     *
     * @param bookVerb
     */
    void updateBookVerb(BookVerb bookVerb);

    /**
     * 获得推广内容
     *
     * @return
     */
    Object getBookVerb();

    /**
     * 删除推广书籍
     *
     * @param id
     */
    void deleteBookVerb(int id);

}
