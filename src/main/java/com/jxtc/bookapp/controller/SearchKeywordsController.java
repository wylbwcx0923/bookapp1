package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.SearchKeywords;
import com.jxtc.bookapp.service.SearchKeywordsService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/jxapp/keywords")
@Api(value = "关键词搜索统计接口", tags = "关键词搜索统计接口")
public class SearchKeywordsController {

    @Autowired
    private SearchKeywordsService searchKeywordsService;

    @RequestMapping(value = "keywords/rank", method = RequestMethod.GET)
    @ApiOperation(value = "获取搜索关键词排行接口", notes = "获取搜索关键词排行接口", httpMethod = "GET")
    public JXResult searchRank(@ApiParam(value = "搜索的关键词", defaultValue = "", required = false)
                               @RequestParam(value = "keywords", defaultValue = "", required = false) String keywords,
                               @ApiParam(value = "当前页", required = false)
                               @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                               @ApiParam(value = "每页显示数量", required = false)
                               @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        PageResult<SearchKeywords> pageResult = searchKeywordsService.getKeywordsSearchCount(keywords, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", pageResult);
    }

}
