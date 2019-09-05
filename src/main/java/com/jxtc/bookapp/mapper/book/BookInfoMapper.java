package com.jxtc.bookapp.mapper.book;

import com.jxtc.bookapp.entity.BookInfo;
import com.jxtc.bookapp.entity.BookInfoWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface BookInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BookInfoWithBLOBs record);

    int insertSelective(BookInfoWithBLOBs record);

    BookInfoWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BookInfo record);

    int updateByPrimaryKeyWithBLOBs(BookInfoWithBLOBs record);

    int updateByPrimaryKey(BookInfo record);

    BookInfo selectByBookId(@Param("bookId") Integer bookId);

    Integer isVIPBook(@Param("bookId") Integer bookId);

    List<BookInfo> selectBooksLikeKeyWords(@Param("keyWords") String keyWords);

    List<BookInfo> selectBooksLikeBookName(@Param("keyWords") String keyWords);

    List<BookInfo> selectBooksByAuthor(@Param("author") String author);

    List<BookInfo> selectBooksByCategory(@Param("category") String category);

    int countBooksByParams(@Param("bookId") Integer bookId, @Param("bookName") String bookName, @Param("author") String author, @Param("status") Integer status);

    List<BookInfo> selectBooksByParams(@Param("bookId") Integer bookId, @Param("bookName") String bookName, @Param("author") String author, @Param("status") Integer status, @Param("offset") int offset, @Param("size") int size);

    int updateBookInfoByBookId(BookInfo bookInfo);

    List<BookInfo> selectBooksByDes(@Param("keyWords") String keyWords);
}