package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.BookInfo;
import com.jxtc.bookapp.entity.ChapterInfo;
import com.jxtc.bookapp.service.BookInfoService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@Api(value = "图书和章节接口", tags = "图书和章节接口")
@RequestMapping("/jxapp/book")
public class BookController {

    @Autowired
    private BookInfoService bookInfoService;


    @ApiOperation(value = "获取章节详情", notes = "获取章节详情", httpMethod = "GET")
    @RequestMapping(value = "getchapterinfo", method = RequestMethod.GET)
    @ResponseBody
    public JXResult getChapterInfo(@ApiParam(value = "书籍id", required = false)
                                   @RequestParam(value = "bookId", defaultValue = "", required = false) int bookId,
                                   @ApiParam(value = "章节id", required = false)
                                   @RequestParam(value = "chapterId", defaultValue = "", required = false) int chapterId) {
        Map<String, Object> map = bookInfoService.getChapterInfo(bookId, chapterId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", map);
    }


    @ApiOperation(value = "获取图书详情", notes = "获取图书详情", httpMethod = "GET")
    @RequestMapping(value = "getbookinfo", method = RequestMethod.GET)
    @ResponseBody
    public JXResult getChapterInfo(@ApiParam(value = "书籍id", required = false)
                                   @RequestParam(value = "bookId", defaultValue = "", required = false) int bookId) {
        BookInfo bookInfo = bookInfoService.getBookInfoByBookId(bookId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bookInfo);
    }


    @ApiOperation(value = "获取章节列表(分页)", notes = "获取章节列表(分页)", httpMethod = "GET")
    @RequestMapping(value = "getchapterlist", method = RequestMethod.GET)
    @ResponseBody
    public JXResult getChapterInfo(@ApiParam(value = "书籍id", required = false)
                                   @RequestParam(value = "bookId", defaultValue = "", required = false) int bookId,
                                   @ApiParam(value = "当前页", required = false)
                                   @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                   @ApiParam(value = "每页显示数量", required = false)
                                   @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
                                   @ApiParam(value = "排序方式(1.正序,2.倒序)")
                                   @RequestParam(value = "orderBy", defaultValue = "1", required = false) int orderBy) {
        PageResult<ChapterInfo> pageResult = bookInfoService.getChapterListByBookId(bookId, pageIndex, pageSize, orderBy);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", pageResult);
    }

    @ApiOperation(value = "看书和消费接口", notes = "看书和消费接口", httpMethod = "GET")
    @RequestMapping(value = "readBook", method = RequestMethod.GET)
    @ResponseBody
    public JXResult readBook(@ApiParam(value = "书籍id", required = false)
                             @RequestParam(value = "bookId", defaultValue = "", required = false) int bookId,
                             @ApiParam(value = "章节id", required = false)
                             @RequestParam(value = "chapterId", defaultValue = "", required = false) int chapterId,
                             @ApiParam(value = "用户id", required = false)
                             @RequestParam(value = "userId", defaultValue = "", required = false) String userId) {
        Map<String, Object> map = bookInfoService.readBook(bookId, chapterId, userId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", map);
    }

    @ApiOperation(value = "图书搜索接口", notes = "图书搜索接口", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "searchBook", method = RequestMethod.GET)
    public JXResult searchBook(@ApiParam(value = "搜索关键字", required = false)
                               @RequestParam(value = "keyWord", defaultValue = "", required = false) String keyWord,
                               @ApiParam(value = "当前页", required = false)
                               @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                               @ApiParam(value = "每页显示数量", required = false)
                               @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        List<BookInfo> bookInfos = bookInfoService.searchBook(keyWord, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bookInfos);
    }

    @ApiOperation(value = "图书交叉推荐接口", notes = "图书交叉推荐接口", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "recommendBook", method = RequestMethod.GET)
    public JXResult recommendBook(@ApiParam(value = "作者名", required = false)
                                  @RequestParam(value = "author", defaultValue = "", required = false) String author,
                                  @ApiParam(value = "作品类型", required = false)
                                  @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                  @ApiParam(value = "当前页", required = false)
                                  @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                  @ApiParam(value = "每页显示数量", required = false)
                                  @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        PageResult<BookInfo> pageResult=bookInfoService.recommendBook(author, category, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", pageResult);
    }
}
