package com.jxtc.bookapp;

import com.jxtc.bookapp.mapper.app.CanalPopularizeCountMapper;
import com.jxtc.bookapp.mapper.app.OrderMapper;
import com.jxtc.bookapp.service.CanalPopularizeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BookappApplicationTests {

   @Autowired
   private OrderMapper orderMapper;
   @Autowired
   private CanalPopularizeService canalPopularizeService;
   @Autowired
   private CanalPopularizeCountMapper canalPopularizeCountMapper;

    @Test
    public void contextLoads() {

    }

}