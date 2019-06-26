package com.jxtc.bookapp.mapper;

import com.jxtc.bookapp.entity.ChapterInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChapterInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChapterInfo record);

    int insertSelective(ChapterInfo record);

    ChapterInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChapterInfo record);

    int updateByPrimaryKey(ChapterInfo record);

    ChapterInfo selectByBookIdAndChapterId(@Param("bookId") int bookId, @Param("chapterId") int chapterId);

    List<ChapterInfo> selectListByBookIdForPage(@Param("bookId") int bookId, @Param("offset") int offset, @Param("size") int size, @Param("orderBy") int orderBy);

    int countByBookId(@Param("bookId") int bookId);
}