package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.BookInfo;
import com.jxtc.bookapp.entity.BookReview;
import com.jxtc.bookapp.entity.BookReviewExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface BookReviewMapper {
    int countByExample(BookReviewExample example);

    int deleteByExample(BookReviewExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BookReview record);

    int insertSelective(BookReview record);

    List<BookReview> selectByExample(BookReviewExample example);

    BookReview selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BookReview record, @Param("example") BookReviewExample example);

    int updateByExample(@Param("record") BookReview record, @Param("example") BookReviewExample example);

    int updateByPrimaryKeySelective(BookReview record);

    int updateByPrimaryKey(BookReview record);

    List<BookReview> selectPageBookReviews(@Param("bookId") int bookId, @Param("offset") int offset, @Param("size") int size);

    void praise(@Param("id") int id, @Param("praise") int praise);

    List<BookReview> selectPageUserBookReviews(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);

    List<BookInfo> selectHaveReviewBookList(@Param("bookName") String bookName, @Param("offset") int offset, @Param("size") int size);

    int countHaveReviewBook(@Param("bookName") String bookName);

    int countByBookId(@Param("bookId") Integer bookId);
}