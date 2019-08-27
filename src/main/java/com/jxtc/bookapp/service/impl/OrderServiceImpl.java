package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.entity.Order;
import com.jxtc.bookapp.entity.OrderCount;
import com.jxtc.bookapp.entity.OrderExample;
import com.jxtc.bookapp.entity.PageCountTotal;
import com.jxtc.bookapp.mapper.app.OrderMapper;
import com.jxtc.bookapp.service.OrderService;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


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
        System.out.println("书名搜索的书名为:" + bookName);
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

    @Override
    public PageResult<OrderCount> getOrderDayList(String startTime, String endTime, int pageIndex, int pageSize) {
        PageResult<OrderCount> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        int total = orderMapper.countDays(startTime, endTime);
        page.setTotal(total);
        //普通订单列表
        int offset = (pageIndex - 1) * pageSize;
        List<OrderCount> orderCounts = orderMapper.selectOrderListByDay(3, startTime, endTime, offset, pageSize);
        page.setPageList(orderCounts);
        return page;
    }

    @Override
    public Map<String, Object> getMonthOrder() {
        Map<String, Object> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //查询当日
        List<Order> todayOrders = orderMapper.selectOrderByCreateTime(sdf.format(calendar.getTime()));
        //查询昨日
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        List<Order> yesterdayOrders = orderMapper.selectOrderByCreateTime(sdf.format(calendar.getTime()));
        //查询本月
        SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
        List<Order> monthOrders = orderMapper.selectOrderByCreateTime(sdfMonth.format(new Date()));
        Map<String, Object> today = getOrderSumAndCount(todayOrders);
        map.put("today", today);
        Map<String, Object> yesterday = getOrderSumAndCount(yesterdayOrders);
        map.put("yesterday", yesterday);
        Map<String, Object> month = getOrderSumAndCount(monthOrders);
        map.put("month", month);
        return map;
    }

    private Map<String, Object> getOrderSumAndCount(List<Order> orders) {
        Map<String, Object> map = new HashMap<>();
        if (orders != null && orders.size() > 0) {
            map.put("count", orders.size());
            double sum = 0;
            for (Order order : orders) {
                sum += order.getAmount();
            }
            map.put("sum", sum / 100);
        } else {
            map.put("count", 0);
            map.put("sum", 0.0);
        }
        return map;
    }


}
