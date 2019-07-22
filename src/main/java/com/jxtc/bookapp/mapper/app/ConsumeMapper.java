package com.jxtc.bookapp.mapper.app;

import com.jxtc.bookapp.entity.Consume;
import com.jxtc.bookapp.entity.ConsumeCount;
import com.jxtc.bookapp.entity.ConsumeExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsumeMapper {
    int countByExample(ConsumeExample example, @Param("tableIndex") int tableIndex);

    int deleteByExample(ConsumeExample example, @Param("tableIndex") int tableIndex);

    int deleteByPrimaryKey(Integer id, @Param("tableIndex") int tableIndex);

    int insert(Consume record, @Param("tableIndex") int tableIndex);

    int insertSelective(Consume record);

    List<Consume> selectByExample(ConsumeExample example, @Param("tableIndex") int tableIndex);

    Consume selectByPrimaryKey(Integer id, @Param("tableIndex") int tableIndex);

    int updateByExampleSelective(@Param("record") Consume record, @Param("example") ConsumeExample example, @Param("tableIndex") int tableIndex);

    int updateByExample(@Param("record") Consume record, @Param("example") ConsumeExample example, @Param("tableIndex") int tableIndex);

    int updateByPrimaryKeySelective(Consume record, @Param("tableIndex") int tableIndex);

    int updateByPrimaryKey(Consume record, @Param("tableIndex") int tableIndex);

    List<Consume> selectListByUserId(@Param("userId") String userId, @Param("tableIndex") Integer tableIndex, @Param("offset") int offset, @Param("size") int size);

    int countByUserId(@Param("userId") String userId, @Param("tableIndex") Integer tableIndex);

    List<ConsumeCount> selectBookConsumeList(@Param("bookName") String bookName, @Param("tableIndex") int tableIndex, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("offset") int offset, @Param("size") int size);

    int countBookConsumes(@Param("bookName") String bookName, @Param("tableIndex") int tableIndex);

    List<ConsumeCount> selectBookChapterConsumesList(@Param("bookId") int bookId, @Param("tableIndex") Integer tableIndex, @Param("offset") int offset, @Param("size") int size);

    int countBookChapterConsumes(@Param("bookId") int bookId, @Param("tableIndex") Integer tableIndex);
}