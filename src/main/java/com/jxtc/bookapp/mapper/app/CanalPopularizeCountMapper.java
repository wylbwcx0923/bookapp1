package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.CanalPopularizeCount;
import com.jxtc.bookapp.entity.CanalPopularizeCountExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CanalPopularizeCountMapper {
    int countByExample(CanalPopularizeCountExample example);

    int deleteByExample(CanalPopularizeCountExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CanalPopularizeCount record);

    int insertSelective(CanalPopularizeCount record);

    List<CanalPopularizeCount> selectByExample(CanalPopularizeCountExample example);

    CanalPopularizeCount selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CanalPopularizeCount record, @Param("example") CanalPopularizeCountExample example);

    int updateByExample(@Param("record") CanalPopularizeCount record, @Param("example") CanalPopularizeCountExample example);

    int updateByPrimaryKeySelective(CanalPopularizeCount record);

    int updateByPrimaryKey(CanalPopularizeCount record);

    Integer countCanalPopularize(@Param("canalId") Integer canalId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<CanalPopularizeCount> selectCanalPopularizeList(@Param("canalId") Integer canalId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("offset") int offset, @Param("size") int size);
}