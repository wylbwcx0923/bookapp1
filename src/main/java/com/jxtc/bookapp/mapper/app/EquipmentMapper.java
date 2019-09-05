package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.Equipment;
import com.jxtc.bookapp.entity.EquipmentExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface EquipmentMapper {
    int countByExample(EquipmentExample example);

    int deleteByExample(EquipmentExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Equipment record);

    int insertSelective(Equipment record);

    List<Equipment> selectByExample(EquipmentExample example);

    Equipment selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Equipment record, @Param("example") EquipmentExample example);

    int updateByExample(@Param("record") Equipment record, @Param("example") EquipmentExample example);

    int updateByPrimaryKeySelective(Equipment record);

    int updateByPrimaryKey(Equipment record);

    Integer selectCountByCanalIdAndCreateTime(@Param("canalId") Integer canalId, @Param("createTime") String createTime);
}