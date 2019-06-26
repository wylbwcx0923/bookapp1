package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.BookReview;
import com.jxtc.bookapp.utils.PageResult;

/**
 * 书籍评论服务
 */
public interface BookReviewService {


    /**
     * 添加书评
     *
     * @param bookReview
     */
    void insert(BookReview bookReview);

    /**
     * 书评列表
     *
     * @param pageIndex
     * @param pageSize
     * @param bookId
     * @return
     */
    PageResult<BookReview> getBookReviewList(String userId, int pageIndex, int pageSize, int bookId);

    /**
     * 书评详情
     *
     * @param id
     * @return
     */
    BookReview getBookReview(String userId, int id);

    /**
     * 我的书评
     *
     * @param pageIndex
     * @param pageSize
     * @param userId
     * @return
     */
    PageResult<BookReview> getMyselfBookReview(int pageIndex, int pageSize, String userId);


    /**
     * 删除我的书评
     *
     * @param id
     */
    void deleteMyselfBookReview(int id);

    /**
     * 书评点赞
     *
     * @param id
     * @param type
     */
    void praise(String userId, int id, int type);


}
