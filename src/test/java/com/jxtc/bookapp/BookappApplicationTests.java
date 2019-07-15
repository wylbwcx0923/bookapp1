package com.jxtc.bookapp;

import com.jxtc.bookapp.service.SmsService;
import com.jxtc.bookapp.utils.Ftp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookappApplicationTests {

    @Autowired
    private SmsService smsService;

    @Test
    public void contextLoads() {

    }

    private void fileUpdateLoad() {
        String host = "47.99.114.219";
        int port = 22;
        String username = "root";
        String password = "evertqIhmxxTl033tpHh";
        try {
//            Ftp ftp = Ftp.getSftpUtil(host, port, username, password);
//            String directory = "/root/test_20190715";
//            String uploadFile = "";
//            ftp.upload(directory, uploadFile);
//            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
