package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.Order;
import com.jxtc.bookapp.entity.OrderExample;
import com.jxtc.bookapp.entity.PageCountTotal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int countByExample(OrderExample example);

    int deleteByExample(OrderExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    List<Order> selectByExample(OrderExample example);

    Order selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderExample example);

    int updateByExample(@Param("record") Order record, @Param("example") OrderExample example);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    List<Order> selectOrderListByParams(@Param("userId") String userId, @Param("bookName") String bookName, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("offset") int offset, @Param("size") int size);

    PageCountTotal countAndSumOrders(@Param("userId") String userId, @Param("bookName") String bookName, @Param("startTime") String startTime, @Param("endTime") String endTime);

    int countStatusOrders(@Param("status") int status, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<Order> selectOrderListByStatus(@Param("status") int status, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("offset") int offset, @Param("size") int size);
}