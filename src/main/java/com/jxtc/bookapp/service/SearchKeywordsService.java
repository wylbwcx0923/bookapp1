package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.SearchKeywords;
import com.jxtc.bookapp.utils.PageResult;

/**
 * 用户搜索关键词统计服务
 */
public interface SearchKeywordsService {
    /**
     * 添加用户搜索的关键词
     *
     * @param keywords
     */
    void addKeyWords(String keywords);

    /**
     * 统计用户搜索关键词的的排行
     * 统计用户搜索关键词的的排行
     *
     * @param keywords
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<SearchKeywords> getKeywordsSearchCount(String keywords, int pageIndex, int pageSize);
}
