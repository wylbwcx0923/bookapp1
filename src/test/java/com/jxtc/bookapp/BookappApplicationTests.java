package com.jxtc.bookapp;

import com.jxtc.bookapp.entity.BookReview;
import com.jxtc.bookapp.entity.OrderCount;
import com.jxtc.bookapp.mapper.app.OrderMapper;
import net.sf.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookappApplicationTests {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void contextLoads() {
        List<OrderCount> orderCounts = orderMapper.selectOrderListByDay(3, null, null);
        for (OrderCount orderCount : orderCounts) {
            System.out.println(orderCount);
        }
    }

}