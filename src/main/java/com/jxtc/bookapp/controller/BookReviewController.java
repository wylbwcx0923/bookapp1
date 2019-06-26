package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.BookReview;
import com.jxtc.bookapp.service.BookReviewService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "图书评论接口", tags = "图书评论接口")
@RestController
@RequestMapping("/jxapp/bookreview")
public class BookReviewController {

    @Autowired
    private BookReviewService bookReviewService;

    @ApiOperation(value = "添加图书评论", notes = "添加图书评论", httpMethod = "POST")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public JXResult addBookReview(@ApiParam(value = "图书评论对象", required = true) @RequestBody BookReview bookReview) {
        bookReviewService.insert(bookReview);
        return new JXResult(true, ApiConstant.StatusCode.OK, "添加书评成功");
    }

    @ApiOperation(value = "获得某本书的书评列表", notes = "获得某本书的书评列表", httpMethod = "GET")
    @RequestMapping(value = "list/book", method = RequestMethod.GET)
    public JXResult getBookReviewList(@ApiParam(value = "用户id", required = false)
                                      @RequestParam(value = "userId", required = false, defaultValue = "") String userId,
                                      @ApiParam(value = "书籍id", required = false)
                                      @RequestParam(value = "bookId", defaultValue = "", required = false) int bookId,
                                      @ApiParam(value = "当前页", required = false)
                                      @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                      @ApiParam(value = "每页显示数量", required = false)
                                      @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        PageResult<BookReview> bookReviewList = bookReviewService.getBookReviewList(userId, pageIndex, pageSize, bookId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bookReviewList);
    }

    @ApiOperation(value = "获得某条评论的详情", notes = "获得某条评论的详情", httpMethod = "GET")
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public JXResult getBookReviewInfo(@ApiParam(value = "用户id", required = false)
                                      @RequestParam(value = "userId", required = false, defaultValue = "") String userId,
                                      @ApiParam(value = "书评id", required = false)
                                      @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        BookReview bookReview = bookReviewService.getBookReview(userId, id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bookReview);
    }

    @ApiOperation(value = "获得某个用户自己的书评列表", notes = "获得某个用户自己的书评列表", httpMethod = "GET")
    @RequestMapping(value = "list/user", method = RequestMethod.GET)
    public JXResult getUserBookReviewList(@ApiParam(value = "用户Id", required = false)
                                          @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                                          @ApiParam(value = "当前页", required = false)
                                          @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                          @ApiParam(value = "每页显示数量", required = false)
                                          @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        PageResult<BookReview> bookReviewList = bookReviewService.getMyselfBookReview(pageIndex, pageSize, userId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bookReviewList);
    }

    @ApiOperation(value = "删除我的书评", notes = "删除我的书评", httpMethod = "DELETE")
    @RequestMapping(value = "del", method = RequestMethod.DELETE)
    public JXResult delBookReview(@ApiParam(value = "书评id", required = false)
                                  @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        bookReviewService.deleteMyselfBookReview(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }


    @ApiOperation(value = "给书评点赞", notes = "给书评点赞", httpMethod = "PUT")
    @RequestMapping(value = "praise", method = RequestMethod.PUT)
    public JXResult praiseBookReview(@ApiParam(value = "用户id", required = false)
                                     @RequestParam(value = "userId", required = false, defaultValue = "") String userId,
                                     @ApiParam(value = "书评id", required = false)
                                     @RequestParam(value = "id", defaultValue = "", required = false) int id,
                                     @ApiParam(value = "类型(1.点赞,2.取消点赞)", required = false)
                                     @RequestParam(value = "type", defaultValue = "", required = false) int type) {
        bookReviewService.praise(userId, id, type);
        return new JXResult(true, ApiConstant.StatusCode.OK, "点赞或取消赞成功");
    }
}
