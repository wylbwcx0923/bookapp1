package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.SearchKeywords;
import com.jxtc.bookapp.mapper.app.SearchKeywordsMapper;
import com.jxtc.bookapp.service.SearchKeywordsService;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service(value = "searchKeywordService")
public class SearchKeywordsServiceImpl implements SearchKeywordsService {
    @Autowired
    private SearchKeywordsMapper searchKeywordsMapper;

    @Override
    public void addKeyWords(String keywords) {
        System.out.println(keywords);
        SearchKeywords searchKeywords = new SearchKeywords();
        searchKeywords.setKeyWords(keywords);
        searchKeywords.setSearchTime(new Date());
        searchKeywordsMapper.insertSelective(searchKeywords);
    }

    @Override
    public PageResult<SearchKeywords> getKeywordsSearchCount(String keywords, int pageIndex, int pageSize) {
        PageResult<SearchKeywords> pageResult = new PageResult<>();
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);
        int total = searchKeywordsMapper.countKeywords(keywords);
        pageResult.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<SearchKeywords> list = searchKeywordsMapper.selectSearchKeywordsCounts(keywords, offset, pageSize);
        pageResult.setPageList(list);
        return pageResult;
    }
}
