package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.Canal;
import com.jxtc.bookapp.entity.CanalExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CanalMapper {
    int countByExample(CanalExample example);

    int deleteByExample(CanalExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Canal record);

    int insertSelective(Canal record);

    List<Canal> selectByExample(CanalExample example);

    Canal selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Canal record, @Param("example") CanalExample example);

    int updateByExample(@Param("record") Canal record, @Param("example") CanalExample example);

    int updateByPrimaryKeySelective(Canal record);

    int updateByPrimaryKey(Canal record);

    Canal selectNewCanal();
}