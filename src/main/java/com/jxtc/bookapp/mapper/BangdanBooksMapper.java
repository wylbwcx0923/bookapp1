package com.jxtc.bookapp.mapper;

import com.jxtc.bookapp.entity.BangdanBooks;
import com.jxtc.bookapp.entity.BangdanBooksExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface BangdanBooksMapper {
    int countByExample(BangdanBooksExample example);

    int deleteByExample(BangdanBooksExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BangdanBooks record);

    int insertSelective(BangdanBooks record);

    List<BangdanBooks> selectByExample(BangdanBooksExample example);

    BangdanBooks selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BangdanBooks record, @Param("example") BangdanBooksExample example);

    int updateByExample(@Param("record") BangdanBooks record, @Param("example") BangdanBooksExample example);

    int updateByPrimaryKeySelective(BangdanBooks record);

    int updateByPrimaryKey(BangdanBooks record);

    List<BangdanBooks> selectByBangDanId(@Param("bangDanId") int bangDanId, @Param("offset") int offset, @Param("size") int size);
}