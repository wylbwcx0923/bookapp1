package com.jxtc.bookapp;

import com.jxtc.bookapp.service.SmsService;
import com.jxtc.bookapp.utils.PayUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.sns.SnsToken;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookappApplicationTests {

    @Autowired
    private SmsService smsService;

    @Test
    public void contextLoads() {
        String nonce_str = PayUtil.create_nonce_str();
        System.out.println("生成的字符串为:" + nonce_str + "\t长度为:" + nonce_str.length());
    }

}
