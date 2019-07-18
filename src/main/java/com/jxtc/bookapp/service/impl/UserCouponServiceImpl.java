package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.entity.UserCoupon;
import com.jxtc.bookapp.entity.UserCouponExample;
import com.jxtc.bookapp.mapper.UserCouponMapper;
import com.jxtc.bookapp.service.UserCouponService;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class UserCouponServiceImpl implements UserCouponService {

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    public PageResult<UserCoupon> getCouponByUserIdAndStatus(int status, String userId, int pageIndex, int pageSize) {
        //查看用户未使用的优惠券是否过期
        expireCoupon(userId);
        //获得不同状态的优惠券列表
        PageResult<UserCoupon> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        UserCouponExample example = new UserCouponExample();
        example.createCriteria().andCouponTypeEqualTo(status).andUserIdEqualTo(userId);
        int total = userCouponMapper.countByExample(example);
        page.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<UserCoupon> userCoupons = userCouponMapper.selectByStatusAndUserId(status, userId, offset, pageSize);
        page.setPageList(userCoupons);
        return page;
    }

    /**
     * 过期掉用户没有使用但过期的优惠券
     *
     * @param userId
     */
    private void expireCoupon(String userId) {
        System.out.println("进行优惠券过期");
        //查询该用户未过期的优惠券
        Date now = new Date();
        UserCouponExample example = new UserCouponExample();
        example.createCriteria().andStatusEqualTo(ApiConstant.CouponStatus.UNUSE).andUserIdEqualTo(userId);
        List<UserCoupon> couponList = userCouponMapper.selectByExample(example);
        if (couponList != null && couponList.size() > 0) {
            System.out.println("没有过期和使用的优惠券"+couponList.size());
            //说明用户有未使用的优惠券
            for (UserCoupon userCoupon : couponList) {
                if (userCoupon.getExpireTime().getTime() < now.getTime()) {
                    //该优惠券已过期
                    UserCoupon coupon = new UserCoupon();
                    coupon.setId(userCoupon.getId());
                    coupon.setStatus(ApiConstant.CouponStatus.EXPIRE);
                    userCouponMapper.updateByPrimaryKeySelective(coupon);
                }
            }
        }
    }

    @Override
    public void useCouponById(int couponId) {
        UserCoupon coupon = new UserCoupon();
        coupon.setId(couponId);
        coupon.setStatus(ApiConstant.CouponStatus.USED);
        userCouponMapper.updateByPrimaryKeySelective(coupon);
    }

    @Override
    public void sendCouponToUser(String userId, int couponType, double discount, double voucher, int reward, String limit, int full, String des, int timer) {
        UserCoupon coupon = new UserCoupon();
        coupon.setStatus(ApiConstant.CouponStatus.UNUSE);//赠送默认为未使用
        coupon.setCouponType(couponType);
        coupon.setDes(des);
        coupon.setDiscount(discount);
        coupon.setFull(full);
        coupon.setLimit(limit);
        coupon.setReward(reward);
        coupon.setUserId(userId);
        coupon.setVoucher(voucher);
        coupon.setCreateTime(new Date());
        //处理过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, timer);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        coupon.setExpireTime(calendar.getTime());
        userCouponMapper.insertSelective(coupon);
    }
}
