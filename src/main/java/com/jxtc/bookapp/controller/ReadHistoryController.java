package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.ReadHistory;
import com.jxtc.bookapp.service.ReadHistoryService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(value = "阅读记录接口", tags = "阅读记录接口")
@RestController
@RequestMapping("/jxapp/readhistory")
public class ReadHistoryController {

    @Autowired
    private ReadHistoryService readHistoryService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiOperation(value = "添加阅读记录", notes = "添加阅读记录", httpMethod = "POST")
    public JXResult addHistory(@ApiParam(value = "阅读记录", required = true)
                               @RequestBody ReadHistory readHistory) {
        readHistoryService.insert(readHistory);
        return new JXResult(true, ApiConstant.StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "del", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除阅读记录", notes = "删除阅读记录", httpMethod = "DELETE")
    public JXResult delHistory(@ApiParam(value = "多个历史记录的id", required = false)
                               @RequestParam(value = "ids[]", defaultValue = "", required = false) int[] ids) {
        readHistoryService.delete(ids);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }

    @ApiOperation(value = "获得用户历史记录的列表", notes = "获得用户历史记录的列表", httpMethod = "GET")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JXResult getList(@ApiParam(value = "用户Id", required = false)
                            @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                            @ApiParam(value = "当前页", required = false)
                            @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                            @ApiParam(value = "每页显示数量", required = false)
                            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        PageResult<ReadHistory> list = readHistoryService.getReadHistoryList(pageIndex, pageSize, userId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", list);
    }

    @ApiOperation(value = "判断该用户是否阅读过这本书", notes = "判断该用户是否阅读过这本书", httpMethod = "GET")
    @RequestMapping(value = "isRead", method = RequestMethod.GET)
    public JXResult isRead(@ApiParam(value = "用户Id", required = false)
                           @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                           @ApiParam(value = "书籍Id", required = false)
                           @RequestParam(value = "bookId", defaultValue = "1", required = false) int bookId) {
        Map<String, Object> readBook = readHistoryService.isReadBook(userId, bookId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", readBook);
    }

}
