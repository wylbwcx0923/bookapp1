package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.ToutiaoCount;

public interface ToutiaoCountService {

    /**
     * 添加头条统计
     *
     * @param toutiaoCount
     */
    void addTaotiaoCount(ToutiaoCount toutiaoCount);
}
