package com.jxtc.bookapp.mapper;

import com.jxtc.bookapp.entity.ReadHistory;
import com.jxtc.bookapp.entity.ReadHistoryExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ReadHistoryMapper {
    int countByExample(ReadHistoryExample example);

    int deleteByExample(ReadHistoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ReadHistory record);

    int insertSelective(ReadHistory record);

    List<ReadHistory> selectByExample(ReadHistoryExample example);

    ReadHistory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ReadHistory record, @Param("example") ReadHistoryExample example);

    int updateByExample(@Param("record") ReadHistory record, @Param("example") ReadHistoryExample example);

    int updateByPrimaryKeySelective(ReadHistory record);

    int updateByPrimaryKey(ReadHistory record);

    List<ReadHistory> selectReadHistoryList(@Param("offset") int offset, @Param("size") int size, @Param("userId") String userId);
}