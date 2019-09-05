package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Equipment;

/**
 * @author 不忘初心
 * 统计设备数量服务接口
 */
public interface EquipmentService {
    /**
     * 添加设备编码
     *
     * @param equipment
     */
    void addEquipment(Equipment equipment);

    Integer getEquipmentCountByCanalIdAndCreateTime(Integer canalId, String createTime);
}
