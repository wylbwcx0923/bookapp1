package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.UserCoupon;
import com.jxtc.bookapp.entity.UserCouponExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCouponMapper {
    int countByExample(UserCouponExample example);

    int deleteByExample(UserCouponExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserCoupon record);

    int insertSelective(UserCoupon record);

    List<UserCoupon> selectByExample(UserCouponExample example);

    UserCoupon selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UserCoupon record, @Param("example") UserCouponExample example);

    int updateByExample(@Param("record") UserCoupon record, @Param("example") UserCouponExample example);

    int updateByPrimaryKeySelective(UserCoupon record);

    int updateByPrimaryKey(UserCoupon record);

    List<UserCoupon> selectByStatusAndUserId(@Param("status") int status, @Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);
}