package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.Order;
import com.jxtc.bookapp.entity.OrderExample;
import com.jxtc.bookapp.entity.PageCountTotal;
import com.jxtc.bookapp.mapper.OrderMapper;
import com.jxtc.bookapp.service.OrderService;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void insertOrder(Order order) {
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.insertSelective(order);
    }

    @Override
    public Order findOrderByOrderId(String orderId) {
        OrderExample example = new OrderExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<Order> orders = orderMapper.selectByExample(example);
        if (orders != null && orders.size() > 0) {
            return orders.get(0);
        }
        return null;
    }

    @Override
    public PageResult<Order> getOrderByParams(String userId, String bookName, String startTime, String endTime, int pageIndex, int pageSize) {
        PageResult<Order> pageResult = new PageResult<>();
        //当前第几页
        pageResult.setPageIndex(pageIndex);
        //每页显示数量
        pageResult.setPageSize(pageSize);
        //总共有多少条数据
        PageCountTotal pageCountTotal = orderMapper.countAndSumOrders(userId, bookName, startTime, endTime);
        pageResult.setTotal(pageCountTotal.getTotal());
        //获得该页显示的所有数据
        int offset = (pageIndex - 1) * pageSize;
        List<Order> orders = orderMapper.selectOrderListByParams(userId, bookName, startTime, endTime, offset, pageSize);
        pageResult.setPageList(orders);
        //设置查询的总金额
        double amount = pageCountTotal.getAmount() / 100;
        pageResult.setParam(amount);
        return pageResult;
    }

    @Override
    public PageResult<Order> getOrderByStatus(int status, String startTime, String endTime, int pageIndex, int pageSize) {
        PageResult<Order> pageResult = new PageResult<>();
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);
        int total = orderMapper.countStatusOrders(status, startTime, endTime);
        pageResult.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<Order> orders = orderMapper.selectOrderListByStatus(status, startTime, endTime, offset, pageSize);
        pageResult.setPageList(orders);
        return pageResult;
    }


}
