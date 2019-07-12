package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.UserCoupon;
import com.jxtc.bookapp.utils.PageResult;

public interface UserCouponService {

    /**
     * 根据优惠券的状态和用户Id获得优惠券列表
     *
     * @param status
     * @param userId
     * @return
     */
    PageResult<UserCoupon> getCouponByUserIdAndStatus(int status, String userId, int pageIndex, int pageSize);

    /**
     * 使用优惠券
     *
     * @param couponId
     */
    void useCouponById(int couponId);

    /**
     * 送优惠券
     *
     * @param userId     用户Id
     * @param couponType 优惠券类型
     * @param discount   折扣券金额,couponType=1时取此参数
     * @param voucher    代金券金额,couponType=2时取此参数
     * @param reward     打赏券类型,couponType=4时取此参数(1.比心,2.玫瑰,3.666,4豪车)
     * @param limit      是否限制普通充值可用(1.限制(如果此参数为1,那么该券只能普通充值可用,VIP不可用),0.不限制)
     * @param full       满多少金额可用(如果该项为空则不限制)
     * @param des        描述(如:"升级大礼包")
     * @param timer      有效时长
     */
    void sendCouponToUser(String userId, int couponType, double discount, double voucher, int reward, String limit, int full, String des, int timer);

}
