package com.jxtc.bookapp;

import com.jxtc.bookapp.service.SmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BookappApplicationTests {

    @Autowired
    private SmsService smsService;

    @Test
    public void contextLoads() {
        String userId = "jx100001";
        Integer tableIndex = Integer.valueOf(userId.substring(userId.length() - 1));
        System.out.println(tableIndex);
    }
}