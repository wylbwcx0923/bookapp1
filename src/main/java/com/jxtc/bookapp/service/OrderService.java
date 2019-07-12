package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Order;
import com.jxtc.bookapp.utils.PageResult;

public interface OrderService {

    /**
     * 插入一条订单
     *
     * @param order
     */
    void insertOrder(Order order);

    /**
     * 通过订单Id查找订单
     *
     * @param orderId
     * @return
     */
    Order findOrderByOrderId(String orderId);



    /**
     * 跟据不同的参数查询订单
     *
     * @param userId
     * @param bookName
     * @param startTime
     * @param endTime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<Order> getOrderByParams(String userId, String bookName, String startTime, String endTime, int pageIndex, int pageSize);


    /**
     * 根据订单的不同状态查询订单
     *
     * @param status
     * @param startTime
     * @param endTime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<Order> getOrderByStatus(int status, String startTime, String endTime, int pageIndex, int pageSize);
}
