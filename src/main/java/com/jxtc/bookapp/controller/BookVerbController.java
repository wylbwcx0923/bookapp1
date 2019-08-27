package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.BookVerb;
import com.jxtc.bookapp.service.BookVerbService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/jxapp/bookverb")
@Api(value = "图书推广接口", tags = "图书推广接口")
public class BookVerbController {
    @Autowired
    private BookVerbService bookVerbService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiOperation(value = "添加推广对象", notes = "添加推广对象", httpMethod = "POST")
    public JXResult addBookVerb(@ApiParam(value = "推广对象", required = true)
                                @RequestBody BookVerb bookVerb) {
        bookVerbService.addBookVerb(bookVerb);
        return new JXResult(true, ApiConstant.StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiOperation(value = "修改推广对象", notes = "修改推广对象", httpMethod = "PUT")
    public JXResult updateBookVerb(@ApiParam(value = "推广对象", required = true)
                                   @RequestBody BookVerb bookVerb) {
        bookVerbService.updateBookVerb(bookVerb);
        return new JXResult(true, ApiConstant.StatusCode.OK, "修改成功");
    }

    @ApiOperation(value = "查询图书推广", notes = "查询图书推广", httpMethod = "GET")
    @RequestMapping(value = "get", method = RequestMethod.GET)
    public JXResult getBookVerb() {
        Object bookVerb = bookVerbService.getBookVerb();
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bookVerb);
    }

    @ApiOperation(value = "删除图书推广", notes = "删除图书推广", httpMethod = "DELETE")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public JXResult delBookVerb(@ApiParam(value = "id", defaultValue = "", required = false)
                                @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        bookVerbService.deleteBookVerb(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }
}
