package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.BookInfo;
import com.jxtc.bookapp.entity.ChapterInfo;
import com.jxtc.bookapp.utils.PageResult;

import java.util.List;
import java.util.Map;

public interface BookInfoService {
    //获得章节详情
    Map<String, Object> getChapterInfo(Integer bookId, Integer chapterId);

    //获得图书详情
    BookInfo getBookInfoByBookId(Integer bookId);

    //获得章节列表(分页)
    PageResult<ChapterInfo> getChapterListByBookId(Integer bookId, int pageIndex, int pageSize, int orderBy);

    //通过id判断书籍是否为VIP书
    int checkVIPBookByBookId(Integer bookId);

    //看书和消费接口
    Map<String, Object> readBook(Integer bookId, Integer chapterId, String userId);

    //图书搜索
    List<BookInfo> searchBook(String keyWords, int pageIndex, int pageSize);

    //交叉推荐接口
    PageResult<BookInfo> recommendBook(String author, String category, int pageIndex, int pageSize);

    //根据不同的筛选条件查询图书列表
    PageResult<BookInfo> getBookInfoListByParam(Integer bookId, String bookName, String author, Integer status, int pageIndex, int pageSize);

    //获得章节详情,供H5调用
    Map<String, Object> getChapterInfoForH5(int bookId, int chapterId);
}
