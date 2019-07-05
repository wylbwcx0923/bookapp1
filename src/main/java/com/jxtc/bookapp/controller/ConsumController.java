package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Consume;
import com.jxtc.bookapp.entity.ConsumeCount;
import com.jxtc.bookapp.service.ConsumeService;
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
@RequestMapping("jxapp/consume")
@Api(tags = "消费统计接口", value = "消费统计接口")
public class ConsumController {

    @Autowired
    private ConsumeService consumeService;

    @ApiOperation(value = "获取某个用户某个月的消费记录", notes = "获取某个用户某个月的消费记录", httpMethod = "GET")
    @RequestMapping(value = "user/list", method = RequestMethod.GET)
    public JXResult userList(@ApiParam(value = "用户Id", required = false)
                             @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                             @ApiParam(value = "月表的下标(如:yyyyMM)", required = false)
                             @RequestParam(value = "tableIndex", defaultValue = "", required = false) Integer tableIndex,
                             @ApiParam(value = "当前页", required = false)
                             @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                             @ApiParam(value = "每页显示数量", required = false)
                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        PageResult<Consume> page = consumeService.getUserConsumes(userId, tableIndex, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }

    @ApiOperation(value = "获得图书的消费列表", notes = "获得图书的消费列表", httpMethod = "GET")
    @RequestMapping(value = "book/list", method = RequestMethod.GET)
    public JXResult bookConsumeList(@ApiParam(value = "书名", required = false)
                                    @RequestParam(value = "bookName", defaultValue = "", required = false) String bookName,
                                    @ApiParam(value = "查询的起始时间(格式:yyyy-MM-dd)", required = false)
                                    @RequestParam(value = "startTime", defaultValue = "", required = false) String startTime,
                                    @ApiParam(value = "查询的结束时间(格式:yyyy-MM-dd)", required = false)
                                    @RequestParam(value = "endTime", defaultValue = "", required = false) String endTime,
                                    @ApiParam(value = "月表的下标(如:yyyyMM)", required = false)
                                    @RequestParam(value = "tableIndex", defaultValue = "", required = false) Integer tableIndex,
                                    @ApiParam(value = "当前页", required = false)
                                    @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                    @ApiParam(value = "每页显示数量", required = false)
                                    @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        PageResult<ConsumeCount> page = consumeService.getBookConsumeList(bookName, tableIndex, startTime, endTime, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }

    @ApiOperation(value = "获得图书的消费列表", notes = "获得图书的消费列表", httpMethod = "GET")
    @RequestMapping(value = "chapter/list", method = RequestMethod.GET)
    public JXResult bookConsumeList(@ApiParam(value = "书的id", required = false)
                                    @RequestParam(value = "bookId", defaultValue = "", required = false) int bookId,
                                    @ApiParam(value = "月表的下标(如:yyyyMM)", required = false)
                                    @RequestParam(value = "tableIndex", defaultValue = "", required = false) Integer tableIndex,
                                    @ApiParam(value = "当前页", required = false)
                                    @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                    @ApiParam(value = "每页显示数量", required = false)
                                    @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        PageResult<ConsumeCount> page = consumeService.getBookChapterCounts(bookId, tableIndex, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }
}
