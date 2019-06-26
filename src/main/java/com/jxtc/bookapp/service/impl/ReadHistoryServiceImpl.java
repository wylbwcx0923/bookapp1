package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.entity.ReadHistory;
import com.jxtc.bookapp.entity.ReadHistoryExample;
import com.jxtc.bookapp.mapper.ReadHistoryMapper;
import com.jxtc.bookapp.service.ReadHistoryService;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReadHistoryServiceImpl implements ReadHistoryService {

    @Autowired
    private ReadHistoryMapper readHistoryMapper;

    @Override
    public void insert(ReadHistory readHistory) {
        //判断用户阅读的这本书在历史记录中是否存在
        ReadHistoryExample example = new ReadHistoryExample();
        example.createCriteria().andBookIdEqualTo(readHistory.getBookId()).andUserIdEqualTo(readHistory.getUserId());
        List<ReadHistory> readHistories = readHistoryMapper.selectByExample(example);
        if (readHistories != null && readHistories.size() > 0) {//在用户的阅读记录中找到了这本书
            //判断用户当前加入的章节是否比原历史记录中的大
            ReadHistory history = readHistories.get(0);//历史的阅读记录
            if (history.getLastReadChapter() <= readHistory.getLastReadChapter()) {
                ReadHistory his = new ReadHistory();
                his.setUpdateTime(new Date());
                his.setLastReadChapter(readHistory.getLastReadChapter());
                readHistoryMapper.updateByExampleSelective(his, example);
            }
        } else {//找不到这本书
            readHistory.setUpdateTime(new Date());
            readHistoryMapper.insertSelective(readHistory);
        }
    }

    @Override
    public void delete(int[] ids) {
        if (ids != null && ids.length > 0) {
            for (int id : ids) {
                readHistoryMapper.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public PageResult<ReadHistory> getReadHistoryList(int pageIndex, int pageSize, String userId) {
        PageResult<ReadHistory> pageResult = new PageResult<>();
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);
        ReadHistoryExample example = new ReadHistoryExample();
        example.createCriteria().andUserIdEqualTo(userId);
        int total = readHistoryMapper.countByExample(example);
        pageResult.setTotal(total);
        List<ReadHistory> list = readHistoryMapper.selectReadHistoryList((pageIndex - 1) * pageSize, pageSize, userId);
        for (ReadHistory history : list) {
            history.getBookInfo().setPicUrl(ApiConstant.Config.IMGPATH + history.getBookInfo().getPicUrl());
        }
        pageResult.setPageList(list);
        return pageResult;
    }

    @Override
    public Map<String, Object> isReadBook(String userId, int bookId) {
        Map<String, Object> map = new HashMap<>();
        ReadHistoryExample example = new ReadHistoryExample();
        example.createCriteria().andUserIdEqualTo(userId).andBookIdEqualTo(bookId);
        List<ReadHistory> readHistories = readHistoryMapper.selectByExample(example);
        if (readHistories != null && readHistories.size() > 0) {
            ReadHistory history = readHistories.get(0);
            map.put("isRead", true);
            map.put("lastChapter", history.getLastReadChapter());
        } else {
            map.put("isRead", false);
        }
        return map;
    }
}
