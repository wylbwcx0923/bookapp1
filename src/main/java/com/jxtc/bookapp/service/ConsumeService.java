package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Consume;
import com.jxtc.bookapp.entity.ConsumeCount;
import com.jxtc.bookapp.utils.PageResult;

/**
 * 消费统计服务
 */
public interface ConsumeService {

    /**
     * 添加一条消费记录
     *
     * @param userId
     * @param bookId
     * @param chapterId
     */
    void addConsume(String userId, int bookId, int chapterId, int amount);

    /**
     * 获得某个用户的消费记录
     *
     * @param userId
     * @param tableIndex 按月查询,默认显示本月的
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<Consume> getUserConsumes(String userId, Integer tableIndex, int pageIndex, int pageSize);


    /**
     * 获得图书的消费列表
     *
     * @param tableIndex 按月查询,默认显示本月的
     * @param startTime  开始查询的时间
     * @param endTime    结束查询的时间
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<ConsumeCount> getBookConsumeList(String bookName, Integer tableIndex, String startTime, String endTime, int pageIndex, int pageSize);

    /**
     * 获得某本书的每个章节的消费列表
     *
     * @param bookId
     * @param tableIndex
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<ConsumeCount> getBookChapterCounts(int bookId, Integer tableIndex, int pageIndex, int pageSize);
}
