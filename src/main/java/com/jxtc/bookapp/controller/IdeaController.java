package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Idea;
import com.jxtc.bookapp.service.IdeaService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "意见反馈接口", tags = "意见反馈接口")
@RestController
@RequestMapping("/jxapp/idea")
public class IdeaController {

    @Autowired
    private IdeaService ideaService;

    @ApiOperation(value = "添加意见反馈", notes = "添加意见反馈", httpMethod = "POST")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public JXResult addReward(@ApiParam(value = "意见对象", required = true)
                              @RequestBody Idea idea) {
        System.out.println(idea);
        ideaService.insertIdea(idea);
        return new JXResult(true, ApiConstant.StatusCode.OK, "反馈成功");
    }

    @ApiOperation(value = "获取意见反馈列表", notes = "获取意见反馈列表", httpMethod = "GET")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JXResult listIdeas(@ApiParam(value = "当前页", required = false)
                              @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                              @ApiParam(value = "每页显示数量", required = false)
                              @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        PageResult<Idea> page = ideaService.getIdeaList(pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }

    @ApiOperation(value = "根据id获得意见详情", notes = "根据id获得意见详情", httpMethod = "GET")
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public JXResult findById(@ApiParam(value = "意见id", required = false)
                             @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        Idea idea = ideaService.findById(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", idea);
    }


    @ApiOperation(value = "批量删除意见反馈", notes = "批量删除意见反馈", httpMethod = "DELETE")
    @RequestMapping(value = "del", method = RequestMethod.DELETE)
    public JXResult delete(@ApiParam(value = "意见id的数组", required = false)
                           @RequestParam(value = "ids", defaultValue = "", required = false) int[] ids) {
        ideaService.deleteIdea(ids);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }

}
