package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.UserInfo;
import com.jxtc.bookapp.entity.UserInfoExample;
import com.jxtc.bookapp.entity.UserInfoKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserInfoMapper {
    int countByExample(UserInfoExample example);

    int deleteByExample(UserInfoExample example);

    int deleteByPrimaryKey(UserInfoKey key);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    List<UserInfo> selectByExample(UserInfoExample example);

    UserInfo selectByPrimaryKey(UserInfoKey key);

    int updateByExampleSelective(@Param("record") UserInfo record, @Param("example") UserInfoExample example);

    int updateByExample(@Param("record") UserInfo record, @Param("example") UserInfoExample example);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    int getMaxId();

    List<UserInfo> selectUserListByPage(@Param("offset") int offset, @Param("size") int size, @Param("userId") String userId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    int countUserList(@Param("userId") String userId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    int countActiviteUser(@Param("startTime") String startTime, @Param("endTime") String endTime);

    int countDownUser(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<UserCount> selectUserCountList(@Param("offset") int offset, @Param("size") int size);

    int countDays();

    void updateUserUpdateTime(@Param("userId") String userId, @Param("updateTime") Date updateTime);

    int countDownloadByCanalIdAndCreateTime(@Param("canalId") Integer canalId, @Param("oneDay") String oneDay);

    int countUserKeepByTime(@Param("days") Integer days, @Param("nowTime") String nowTime);

    int countUserRegistByTime(@Param("days") Integer days, @Param("nowTime") String nowTime);

    List<UserCount> selectUserCountActisList(@Param("offset") int offset,@Param("size") int size);
}