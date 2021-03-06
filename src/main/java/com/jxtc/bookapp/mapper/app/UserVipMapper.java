package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.UserVip;
import com.jxtc.bookapp.entity.UserVipExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserVipMapper {
    int countByExample(UserVipExample example);

    int deleteByExample(UserVipExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserVip record);

    int insertSelective(UserVip record);

    List<UserVip> selectByExample(UserVipExample example);

    UserVip selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UserVip record, @Param("example") UserVipExample example);

    int updateByExample(@Param("record") UserVip record, @Param("example") UserVipExample example);

    int updateByPrimaryKeySelective(UserVip record);

    int updateByPrimaryKey(UserVip record);
}