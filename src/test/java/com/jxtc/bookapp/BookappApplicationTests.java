package com.jxtc.bookapp;

import com.jxtc.bookapp.entity.Canal;
import com.jxtc.bookapp.entity.CanalPopularizeCount;
import com.jxtc.bookapp.mapper.app.CanalPopularizeCountMapper;
import com.jxtc.bookapp.mapper.app.UserInfoMapper;
import com.jxtc.bookapp.service.CanalPopularizeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BookappApplicationTests {

    @Autowired
    private CanalPopularizeService canalPopularizeService;
    @Autowired
    private CanalPopularizeCountMapper canalPopularizeCountMapper;

    @Test
    public void contextLoads() {
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String oneDay = sdf.format(new Date());
        List<Canal> canals = canalPopularizeService.getCanalList();
        if (canals != null && canals.size() > 0) {
            for (Canal canal : canals) {
                CanalPopularizeCount count = new CanalPopularizeCount();
                count.setOneDay(oneDay);
                count.setNounId(canal.getId());
                count.setDownCount(0);
                count.setPayCount(0);
                canalPopularizeCountMapper.insertSelective(count);
            }
        }*/
    }

}