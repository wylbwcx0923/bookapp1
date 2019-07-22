package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.UserEmpirical;
import com.jxtc.bookapp.entity.UserEmpiricalExample;
import com.jxtc.bookapp.entity.UserEmpiricalKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserEmpiricalMapper {
    int countByExample(UserEmpiricalExample example);

    int deleteByExample(UserEmpiricalExample example);

    int deleteByPrimaryKey(UserEmpiricalKey key);

    int insert(UserEmpirical record);

    int insertSelective(UserEmpirical record);

    List<UserEmpirical> selectByExample(UserEmpiricalExample example);

    UserEmpirical selectByPrimaryKey(UserEmpiricalKey key);

    int updateByExampleSelective(@Param("record") UserEmpirical record, @Param("example") UserEmpiricalExample example);

    int updateByExample(@Param("record") UserEmpirical record, @Param("example") UserEmpiricalExample example);

    int updateByPrimaryKeySelective(UserEmpirical record);

    int updateByPrimaryKey(UserEmpirical record);
}