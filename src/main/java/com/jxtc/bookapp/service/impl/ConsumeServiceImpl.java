package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.Consume;
import com.jxtc.bookapp.entity.ConsumeCount;
import com.jxtc.bookapp.entity.ConsumeExample;
import com.jxtc.bookapp.mapper.BookInfoMapper;
import com.jxtc.bookapp.mapper.ConsumeMapper;
import com.jxtc.bookapp.service.ConsumeService;
import com.jxtc.bookapp.utils.PageResult;
import com.jxtc.bookapp.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ConsumeServiceImpl implements ConsumeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConsumeMapper consumeMapper;
    @Autowired
    private BookInfoMapper bookInfoMapper;

    @Override
    public void addConsume(String userId, int bookId, int chapterId, int amount) {
        int tableIndex = TimeUtil.getTableIndex();
        Consume consume = new Consume();
        consume.setBookId(bookId);
        consume.setUserId(userId);
        consume.setAmount(amount);
        consume.setChapterId(chapterId);
        consume.setCreateTime(new Date());
        consume.setTableIndex(tableIndex);
        consumeMapper.insertSelective(consume);
        logger.info("用户:" + userId + "消费图书:" + bookId + "消费章节:" + chapterId + "花费:" + amount);
    }

    @Override
    public PageResult<Consume> getUserConsumes(String userId, Integer tableIndex, int pageIndex, int pageSize) {
        if (tableIndex == null || tableIndex == 0) {
            //如果没有传入查询的月份,默认查询本月的消费
            tableIndex = TimeUtil.getTableIndex();
        }
        PageResult<Consume> pageResult = new PageResult<>();
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);
        int total = consumeMapper.countByUserId(userId, tableIndex);
        pageResult.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<Consume> consumes = consumeMapper.selectListByUserId(userId, tableIndex, offset, pageSize);
        if (consumes != null && consumes.size() > 0) {
            for (Consume consume : consumes) {
                consume.setBookName(bookInfoMapper.selectByBookId(consume.getBookId()).getBookName());
            }
        }
        pageResult.setPageList(consumes);
        return pageResult;
    }

    @Override
    public PageResult<ConsumeCount> getBookConsumeList(String bookName, Integer tableIndex, String startTime, String endTime, int pageIndex, int pageSize) {
        if (tableIndex == null || tableIndex == 0) {
            //如果没有传入查询的月份,默认查询本月的消费
            tableIndex = TimeUtil.getTableIndex();
        }
        PageResult<ConsumeCount> pageResult = new PageResult<>();
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);
        int total = consumeMapper.countBookConsumes(bookName, tableIndex);
        pageResult.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<ConsumeCount> consumeCounts = consumeMapper.selectBookConsumeList(bookName, tableIndex, startTime, endTime, offset, pageSize);
        pageResult.setPageList(consumeCounts);
        return pageResult;
    }

    @Override
    public PageResult<ConsumeCount> getBookChapterCounts(int bookId, Integer tableIndex, int pageIndex, int pageSize) {
        if (tableIndex == null || tableIndex == 0) {
            //如果没有传入查询的月份,默认查询本月的消费
            tableIndex = TimeUtil.getTableIndex();
        }
        PageResult<ConsumeCount> pageResult = new PageResult<>();
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);
        int total = consumeMapper.countBookChapterConsumes(bookId, tableIndex);
        pageResult.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<ConsumeCount> consumeCounts = consumeMapper.selectBookChapterConsumesList(bookId, tableIndex, offset, pageSize);
        pageResult.setPageList(consumeCounts);
        return pageResult;
    }
}
