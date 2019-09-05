package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.Equipment;
import com.jxtc.bookapp.mapper.app.EquipmentMapper;
import com.jxtc.bookapp.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value = "equipmentService")
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public void addEquipment(Equipment equipment) {
        equipment.setCreateTime(new Date());
        equipmentMapper.insertSelective(equipment);
    }

    @Override
    public Integer getEquipmentCountByCanalIdAndCreateTime(Integer canalId, String createTime) {
        Integer count = equipmentMapper.selectCountByCanalIdAndCreateTime(canalId, createTime);
        return count;
    }
}
