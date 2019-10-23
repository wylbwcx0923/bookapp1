package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.Equipment;
import com.jxtc.bookapp.entity.EquipmentExample;
import com.jxtc.bookapp.entity.ToutiaoCount;
import com.jxtc.bookapp.entity.ToutiaoCountExample;
import com.jxtc.bookapp.mapper.app.EquipmentMapper;
import com.jxtc.bookapp.mapper.app.ToutiaoCountMapper;
import com.jxtc.bookapp.service.EquipmentService;
import com.jxtc.bookapp.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service(value = "equipmentService")
public class EquipmentServiceImpl implements EquipmentService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private ToutiaoCountMapper toutiaoCountMapper;

    @Override
    public void addEquipment(Equipment equipment) {
        String imei = equipment.getImei();
        String md5Imei = DigestUtils.md5DigestAsHex(imei.getBytes());
        logger.info("MD5加密后的IMEI的值为:" + md5Imei);
        ToutiaoCountExample toutiaoCountExample = new ToutiaoCountExample();
        toutiaoCountExample.createCriteria().andImeiEqualTo(md5Imei);
        List<ToutiaoCount> toutiaoCounts = toutiaoCountMapper.selectByExample(toutiaoCountExample);
        if (toutiaoCounts != null && toutiaoCounts.size() > 0) {
            String res = HttpClientUtil.doGet(toutiaoCounts.get(0).getCallbackUrl() + "&event_type=0");
            logger.info("今日头条回调结果:" + res);
        }

        equipment.setCreateTime(new Date());
        EquipmentExample example = new EquipmentExample();
        example.createCriteria().andEquipmentIdEqualTo(equipment.getEquipmentId());
        List<Equipment> equipmentList = equipmentMapper.selectByExample(example);
        if (equipmentList == null || equipmentList.size() <= 0) {
            logger.info("该用户首次安装");
            equipmentMapper.insertSelective(equipment);
        }
    }

    @Override
    public Integer getEquipmentCountByCanalIdAndCreateTime(Integer canalId, String createTime) {
        Integer count = equipmentMapper.selectCountByCanalIdAndCreateTime(canalId, createTime);
        return count;
    }
}
