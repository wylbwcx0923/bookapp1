package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.ReadHistory;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @author wyl
 * 用户阅读记录服务
 */
public interface ReadHistoryService {

    /**
     * 添加用户的阅读记录
     *
     * @param readHistory
     * @return
     */
    void insert(ReadHistory readHistory);

    /**
     * 批量删除阅读记录
     *
     * @param ids
     * @return
     */
    void delete(int[] ids);

    /**
     * 用户阅读记录的列表
     *
     * @param pageIndex
     * @param pageSize
     * @param userId
     * @return
     */
    PageResult<ReadHistory> getReadHistoryList(int pageIndex, int pageSize, String userId);

    /**
     * 判断该用户是否阅读了该本书,如果阅读过则返回阅读的章节
     * @param userId
     * @param bookId
     * @return
     */
    Map<String, Object> isReadBook(String userId, int bookId);
}
