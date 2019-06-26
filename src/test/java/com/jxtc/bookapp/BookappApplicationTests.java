package com.jxtc.bookapp;

import com.jxtc.bookapp.entity.BookInfo;
import com.jxtc.bookapp.mapper.BookInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookappApplicationTests {

    @Autowired
    private BookInfoMapper bookInfoMapper;

    @Test
    public void contextLoads() {
        List<BookInfo> bookInfos = bookInfoMapper.selectBooksByAuthor("六月");
        System.out.println(bookInfos);
    }

}
