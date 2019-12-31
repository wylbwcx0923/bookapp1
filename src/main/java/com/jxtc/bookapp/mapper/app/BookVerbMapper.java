package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.BookVerb;
import com.jxtc.bookapp.entity.BookVerbExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


public interface BookVerbMapper {
    int countByExample(BookVerbExample example);

    int deleteByExample(BookVerbExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BookVerb record);

    int insertSelective(BookVerb record);

    List<BookVerb> selectByExample(BookVerbExample example);

    BookVerb selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BookVerb record, @Param("example") BookVerbExample example);

    int updateByExample(@Param("record") BookVerb record, @Param("example") BookVerbExample example);

    int updateByPrimaryKeySelective(BookVerb record);

    int updateByPrimaryKey(BookVerb record);
}