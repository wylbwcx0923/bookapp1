package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.Bangdan;
import com.jxtc.bookapp.entity.BangdanExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BangdanMapper {
    int countByExample(BangdanExample example);

    int deleteByExample(BangdanExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Bangdan record);

    int insertSelective(Bangdan record);

    List<Bangdan> selectByExample(BangdanExample example);

    Bangdan selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Bangdan record, @Param("example") BangdanExample example);

    int updateByExample(@Param("record") Bangdan record, @Param("example") BangdanExample example);

    int updateByPrimaryKeySelective(Bangdan record);

    int updateByPrimaryKey(Bangdan record);

    List<Bangdan> selectBangDanList(@Param("offset") int offset, @Param("size") int size);

    Bangdan selectBandanByBangDanId(@Param("bangDanId") int bangDanId);
}