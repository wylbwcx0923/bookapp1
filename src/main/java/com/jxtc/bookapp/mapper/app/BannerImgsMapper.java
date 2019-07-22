package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.BannerImgs;
import com.jxtc.bookapp.entity.BannerImgsExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BannerImgsMapper {
    int countByExample(BannerImgsExample example);

    int deleteByExample(BannerImgsExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BannerImgs record);

    int insertSelective(BannerImgs record);

    List<BannerImgs> selectByExample(BannerImgsExample example);

    BannerImgs selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BannerImgs record, @Param("example") BannerImgsExample example);

    int updateByExample(@Param("record") BannerImgs record, @Param("example") BannerImgsExample example);

    int updateByPrimaryKeySelective(BannerImgs record);

    int updateByPrimaryKey(BannerImgs record);

    List<BannerImgs> selectBannerImgs(@Param("bannerId") int bannerId);
}