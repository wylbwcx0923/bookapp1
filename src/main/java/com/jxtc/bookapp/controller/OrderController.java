package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Order;
import com.jxtc.bookapp.entity.OrderCount;
import com.jxtc.bookapp.service.OrderService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "订单接口", value = "订单接口")
@RestController
@RequestMapping("jxapp/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "根据不同的条件获得充值订单", notes = "根据不同的条件获得充值订单", httpMethod = "GET")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JXResult getOrderList(@ApiParam(value = "书名", required = false)
                                 @RequestParam(value = "bookName", defaultValue = "", required = false) String bookName,
                                 @ApiParam(value = "查询的起始时间(格式:yyyy-MM-dd)", required = false)
                                 @RequestParam(value = "startTime", defaultValue = "", required = false) String startTime,
                                 @ApiParam(value = "查询的结束时间(格式:yyyy-MM-dd)", required = false)
                                 @RequestParam(value = "endTime", defaultValue = "", required = false) String endTime,
                                 @ApiParam(value = "用户Id", required = false)
                                 @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                                 @ApiParam(value = "当前页", required = false)
                                 @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                 @ApiParam(value = "每页显示数量", required = false)
                                 @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        System.out.println("书名为:"+bookName);
        PageResult<Order> page = orderService.getOrderByParams(userId,bookName,startTime,endTime,pageIndex,pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }

    @ApiOperation(value = "根据id获得订单详情", notes = "根据id获得订单详情", httpMethod = "GET")
    @RequestMapping(value = "findbyid", method = RequestMethod.GET)
    public JXResult findById(@ApiParam(value = "订单Id", required = false)
                             @RequestParam(value = "orderId", defaultValue = "", required = false) String orderId) {
        Order order = orderService.findOrderByOrderId(orderId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", order);
    }

    @ApiOperation(value = "根据不同的订单状态获得订单列表", notes = "根据不同的订单状态获得订单列表", httpMethod = "GET")
    @RequestMapping(value = "status/list", method = RequestMethod.GET)
    public JXResult getOrderStatusList(@ApiParam(value = "查询的起始时间(格式:yyyy-MM-dd)", required = false)
                                       @RequestParam(value = "startTime", defaultValue = "", required = false) String startTime,
                                       @ApiParam(value = "查询的结束时间(格式:yyyy-MM-dd)", required = false)
                                       @RequestParam(value = "endTime", defaultValue = "", required = false) String endTime,
                                       @ApiParam(value = "订单状态(0.下单未支付,1.支付成功,2.支付失败,3.全部)", required = false)
                                       @RequestParam(value = "status", defaultValue = "3", required = false) int status,
                                       @ApiParam(value = "当前页", required = false)
                                       @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                       @ApiParam(value = "每页显示数量", required = false)
                                       @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        PageResult<Order> page = orderService.getOrderByStatus(status, startTime, endTime, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }

    @ApiOperation(value = "获得每一天的充值金额", notes = "获得每一天的充值金额", httpMethod = "GET")
    @RequestMapping(value = "day/list", method = RequestMethod.GET)
    public JXResult getOrderListByPage(@ApiParam(value = "查询的起始时间(格式:yyyy-MM-dd)", required = false)
                                       @RequestParam(value = "startTime", defaultValue = "", required = false) String startTime,
                                       @ApiParam(value = "查询的结束时间(格式:yyyy-MM-dd)", required = false)
                                       @RequestParam(value = "endTime", defaultValue = "", required = false) String endTime) {
        List<OrderCount> orderDayList = orderService.getOrderDayList(startTime, endTime);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", orderDayList);
    }
}
