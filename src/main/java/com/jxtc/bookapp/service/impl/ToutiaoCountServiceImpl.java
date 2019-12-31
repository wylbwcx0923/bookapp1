package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.ToutiaoCount;
import com.jxtc.bookapp.entity.ToutiaoCountExample;
import com.jxtc.bookapp.mapper.app.ToutiaoCountMapper;
import com.jxtc.bookapp.service.ToutiaoCountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wangyuliang
 */
@Service(value = "toutiaoCountService")
public class ToutiaoCountServiceImpl implements ToutiaoCountService {
    @Autowired
    private ToutiaoCountMapper toutiaoCountMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void addTaotiaoCount(ToutiaoCount toutiaoCount) {
        String imei = toutiaoCount.getImei();
        ToutiaoCountExample example = new ToutiaoCountExample();
        example.createCriteria().andImeiEqualTo(imei);
        List<ToutiaoCount> toutiaoCounts = toutiaoCountMapper.selectByExample(example);
        if (toutiaoCounts == null || toutiaoCounts.size() <= 0) {
            ToutiaoCountExample countExample = new ToutiaoCountExample();
            countExample.createCriteria().andAndroididEqualTo(toutiaoCount.getAndroidid());
            List<ToutiaoCount> toutiaoCounts1 = toutiaoCountMapper.selectByExample(example);
            if (toutiaoCounts1 == null || toutiaoCounts1.size() <= 0) {
                toutiaoCountMapper.insertSelective(toutiaoCount);
            }
        }
        logger.info("存在不插入");
    }
}
