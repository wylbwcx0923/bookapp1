package com.jxtc.bookapp.mapper;

import com.jxtc.bookapp.entity.Banner;
import com.jxtc.bookapp.entity.BannerExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface BannerMapper {
    int countByExample(BannerExample example);

    int deleteByExample(BannerExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Banner record);

    int insertSelective(Banner record);

    List<Banner> selectByExample(BannerExample example);

    Banner selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Banner record, @Param("example") BannerExample example);

    int updateByExample(@Param("record") Banner record, @Param("example") BannerExample example);

    int updateByPrimaryKeySelective(Banner record);

    int updateByPrimaryKey(Banner record);

    List<Banner> selectBannerListByPage(@Param("offset") int offset, @Param("size") int size);
}