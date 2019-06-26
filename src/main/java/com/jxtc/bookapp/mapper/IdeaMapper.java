package com.jxtc.bookapp.mapper;

import com.jxtc.bookapp.entity.Idea;
import com.jxtc.bookapp.entity.IdeaExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IdeaMapper {
    int countByExample(IdeaExample example);

    int deleteByExample(IdeaExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Idea record);

    int insertSelective(Idea record);

    List<Idea> selectByExample(IdeaExample example);

    Idea selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Idea record, @Param("example") IdeaExample example);

    int updateByExample(@Param("record") Idea record, @Param("example") IdeaExample example);

    int updateByPrimaryKeySelective(Idea record);

    int updateByPrimaryKey(Idea record);

    List<Idea> selectIdeasForPage(@Param("offset") int offset, @Param("size") int size);
}