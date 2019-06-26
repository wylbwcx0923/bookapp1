package com.jxtc.bookapp.mapper;

import com.jxtc.bookapp.entity.BookInfo;
import com.jxtc.bookapp.entity.BookInfoWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BookInfoWithBLOBs record);

    int insertSelective(BookInfoWithBLOBs record);

    BookInfoWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BookInfoWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(BookInfoWithBLOBs record);

    int updateByPrimaryKey(BookInfo record);

    BookInfo selectByBookId(@Param("bookId") Integer bookId);

    Integer isVIPBook(@Param("bookId") Integer bookId);

    List<BookInfo> selectBooksLikeKeyWords(@Param("keyWords")String keyWords);

    List<BookInfo> selectBooksLikeBookName(@Param("keyWords")String keyWords);

    List<BookInfo> selectBooksByAuthor(@Param("author")String author);

    List<BookInfo> selectBooksByCategory(@Param("category")String category);

}