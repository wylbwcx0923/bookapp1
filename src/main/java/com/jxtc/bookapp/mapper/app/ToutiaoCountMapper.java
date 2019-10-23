package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.ToutiaoCount;
import com.jxtc.bookapp.entity.ToutiaoCountExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ToutiaoCountMapper {
    int countByExample(ToutiaoCountExample example);

    int deleteByExample(ToutiaoCountExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ToutiaoCount record);

    int insertSelective(ToutiaoCount record);

    List<ToutiaoCount> selectByExample(ToutiaoCountExample example);

    ToutiaoCount selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ToutiaoCount record, @Param("example") ToutiaoCountExample example);

    int updateByExample(@Param("record") ToutiaoCount record, @Param("example") ToutiaoCountExample example);

    int updateByPrimaryKeySelective(ToutiaoCount record);

    int updateByPrimaryKey(ToutiaoCount record);
}