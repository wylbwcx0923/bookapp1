package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.UserCoupon;
import com.jxtc.bookapp.service.UserCouponService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户优惠券接口", tags = "用户优惠券接口")
@RestController
@RequestMapping("jxapp/coupon")
public class UserCouponController {

    @Autowired
    private UserCouponService userCouponService;

    @ApiOperation(value = "根据不同的状态获得优惠券列表", notes = "根据不同的状态获得优惠券列表", httpMethod = "GET")
    @RequestMapping(value = "getCouponByStatus", method = RequestMethod.GET)
    public JXResult getCouponByStatus(@ApiParam(value = "用户Id", required = false)
                                      @RequestParam(value = "userId", required = false, defaultValue = "") String userId,
                                      @ApiParam(value = "优惠券状态", required = false)
                                      @RequestParam(value = "status", required = false, defaultValue = "") int status,
                                      @ApiParam(value = "当前页", required = false)
                                      @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                      @ApiParam(value = "每页显示数量", required = false)
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        PageResult<UserCoupon> page = userCouponService.getCouponByUserIdAndStatus(status, userId, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }

    @ApiOperation(value = "根据优惠券Id使用优惠券", notes = "根据优惠券Id使用优惠券", httpMethod = "PUT")
    @RequestMapping(value = "useCouponById", method = RequestMethod.PUT)
    public JXResult useCouponById(@ApiParam(value = "优惠券Id", required = false)
                                  @RequestParam(value = "couponId", required = false, defaultValue = "") int couponId) {
        userCouponService.useCouponById(couponId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功");
    }

}
