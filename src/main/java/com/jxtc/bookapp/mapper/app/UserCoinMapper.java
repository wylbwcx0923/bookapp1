package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.UserCoin;
import com.jxtc.bookapp.entity.UserCoinExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCoinMapper {
    int countByExample(UserCoinExample example);

    int deleteByExample(UserCoinExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserCoin record);

    int insertSelective(UserCoin record);

    List<UserCoin> selectByExample(UserCoinExample example);

    UserCoin selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UserCoin record, @Param("example") UserCoinExample example);

    int updateByExample(@Param("record") UserCoin record, @Param("example") UserCoinExample example);

    int updateByPrimaryKeySelective(UserCoin record);

    int updateByPrimaryKey(UserCoin record);

    int getCoinByUserId(@Param("userId") String userId);

    int addCoinByUserId(@Param("userId") String userId, @Param("addCoin") int addCoin);
}