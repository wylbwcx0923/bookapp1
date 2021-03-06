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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @ApiOperation(value = "获取章节详情H5调用", notes = "获取章节详情H5调用", httpMethod = "GET")
    @RequestMapping(value = "getchapterinfo/h5", method = RequestMethod.GET)
    @ResponseBody
    public JXResult getChapterInfoForH5(@ApiParam(value = "书籍id", required = false)
                                        @RequestParam(value = "bookId", defaultValue = "", required = false) int bookId,
                                        @ApiParam(value = "章节id", required = false)
                                        @RequestParam(value = "chapterId", defaultValue = "", required = false) int chapterId) {
        Map<String, Object> map = bookInfoService.getChapterInfoForH5(bookId, chapterId);
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
        PageResult<BookInfo> pageResult = bookInfoService.recommendBook(author, category, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", pageResult);
    }

    @ApiOperation(value = "根据不同的条件筛选图书", notes = "根据不同的条件筛选图书", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "booklistbyparams", method = RequestMethod.GET)
    public JXResult getBookListByParams(@ApiParam(value = "作者名", required = false)
                                        @RequestParam(value = "author", defaultValue = "", required = false) String author,
                                        @ApiParam(value = "书名", required = false)
                                        @RequestParam(value = "bookName", defaultValue = "", required = false) String bookName,
                                        @ApiParam(value = "书id", required = false)
                                        @RequestParam(value = "bookId", required = false) Integer bookId,
                                        @ApiParam(value = "书的状态(1.上架,0.下架,2.全部)", required = false)
                                        @RequestParam(value = "status", required = false) Integer status,
                                        @ApiParam(value = "当前页", required = false)
                                        @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                        @ApiParam(value = "每页显示数量", required = false)
                                        @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        System.out.println("author = [" + author + "], bookName = [" + bookName + "], bookId = [" + bookId + "], status = [" + status + "], pageIndex = [" + pageIndex + "], pageSize = [" + pageSize + "]");
        PageResult<BookInfo> pageResult = bookInfoService.getBookInfoListByParam(bookId, bookName, author, status, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", pageResult);
    }

    @ApiOperation(value = "上传图书封面接口", notes = "上传图书封面接口", httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "upload/book/pic", method = RequestMethod.POST)
    public JXResult uploadBookPic(@ApiParam(value = "书id", required = false)
                                  @RequestParam(value = "bookId", required = false) String bookId,
                                  @RequestParam("file") MultipartFile uploadFile) {
        String result = bookInfoService.uploadBookPic(bookId, uploadFile);
        return new JXResult(true, ApiConstant.StatusCode.OK, "上传成功", result);
    }

    @ApiOperation(value = "修改图书信息接口", notes = "修改图书信息接口", httpMethod = "PUT")
    @ResponseBody
    @RequestMapping(value = "update/book/info", method = RequestMethod.PUT)
    public JXResult updateBookInfo(@ApiParam(value = "图书对象", required = true)
                                   @RequestBody BookInfo bookInfo) {
        bookInfoService.updateBookInfo(bookInfo);
        return new JXResult(true, ApiConstant.StatusCode.OK, "修改成功");
    }

    @ApiOperation(value = "修改章节内容接口", notes = "修改章节内容接口", httpMethod = "PUT")
    @ResponseBody
    @RequestMapping(value = "update/chapter/content", method = RequestMethod.PUT)
    public JXResult updateChapterContent(@ApiParam(value = "章节对象", required = true)
                                         @RequestBody Map<String, Object> map) {
        int bookId = (int) map.get("bookId");
        int chapterId = (int) map.get("chapterId");
        String content = (String) map.get("content");
        bookInfoService.updateChapterContent(bookId, chapterId, content);
        return new JXResult(true, ApiConstant.StatusCode.OK, "修改成功");
    }
}
