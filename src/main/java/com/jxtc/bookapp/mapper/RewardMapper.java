package com.jxtc.bookapp.mapper;

import com.jxtc.bookapp.entity.Reward;
import com.jxtc.bookapp.entity.RewardExample;

import java.util.List;
import java.util.Map;

import com.jxtc.bookapp.entity.RewardRank;
import org.apache.ibatis.annotations.Param;

public interface RewardMapper {
    int countByExample(RewardExample example);

    int deleteByExample(RewardExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Reward record);

    int insertSelective(Reward record);

    List<Reward> selectByExample(RewardExample example);

    Reward selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Reward record, @Param("example") RewardExample example);

    int updateByExample(@Param("record") Reward record, @Param("example") RewardExample example);

    int updateByPrimaryKeySelective(Reward record);

    int updateByPrimaryKey(Reward record);

    List<RewardRank> selectTotals(@Param("offset") int offset, @Param("size") int size, @Param("bookId") int bookId);

    int countTotals(@Param("bookId")int bookId);
}