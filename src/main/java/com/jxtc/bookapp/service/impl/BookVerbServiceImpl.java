package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.BookInfo;
import com.jxtc.bookapp.entity.BookVerb;
import com.jxtc.bookapp.entity.BookVerbExample;
import com.jxtc.bookapp.mapper.app.BookVerbMapper;
import com.jxtc.bookapp.service.BookInfoService;
import com.jxtc.bookapp.service.BookVerbService;
import com.jxtc.bookapp.service.RedisService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "bookVerbService")
public class BookVerbServiceImpl implements BookVerbService {

    @Autowired
    private BookVerbMapper bookVerbMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BookInfoService bookInfoService;

    @Override
    public void addBookVerb(BookVerb bookVerb) {
        bookVerb.setCreateTime(new Date());
        bookVerbMapper.insertSelective(bookVerb);
    }

    @Override
    public void updateBookVerb(BookVerb bookVerb) {
        bookVerbMapper.updateByPrimaryKeySelective(bookVerb);
    }

    @Override
    public Object getBookVerb() {
        Map<String, Object> map = new HashMap<>();
        BookVerbExample example = new BookVerbExample();
        example.createCriteria();
        List<BookVerb> bookVerbs = bookVerbMapper.selectByExample(example);
        if (bookVerbs != null && bookVerbs.size() > 0) {
            BookVerb bookVerb = bookVerbs.get(0);
            map.put("id", bookVerb.getId());
            map.put("bookId", bookVerb.getBookId());
            map.put("chapterId", bookVerb.getChapterId());
            BookInfo bookInfo = bookInfoService.getBookInfoByBookId(bookVerb.getBookId());
            map.put("bookInfo", bookInfo);
        }
        return map;
    }

    @Override
    public void deleteBookVerb(int id) {
        bookVerbMapper.deleteByPrimaryKey(id);
    }
}
