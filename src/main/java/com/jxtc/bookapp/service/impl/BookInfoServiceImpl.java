package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.OSSCacheKey;
import com.jxtc.bookapp.config.RedisKey;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.*;
import com.jxtc.bookapp.service.BookInfoService;
import com.jxtc.bookapp.service.FileUploadService;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.service.UserInfoService;
import com.jxtc.bookapp.utils.HttpClientUtil;
import com.jxtc.bookapp.utils.PageResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 图书和章节服务
 */
@Service(value = "bookInfoService")
public class BookInfoServiceImpl implements BookInfoService {

    @Autowired
    private BookInfoMapper bookInfoMapper;
    @Autowired
    private ChapterInfoMapper chapterInfoMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserEmpiricalMapper userEmpiricalMapper;
    @Autowired
    private UserAssetMapper userAssetMapper;
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 获得章节详情
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    @Override
    public Map<String, Object> getChapterInfo(Integer bookId, Integer chapterId) {
        Map<String, Object> map = new HashMap<>();
        //从缓存中获取书籍的章节内容
        String content = getChapterContentByRedis(bookId, chapterId);
        //如果缓存中取不到,那么从阿里云的OSS里面取
        if (StringUtils.isEmpty(content)) {
            String url = download(bookId + "", chapterId + "");
            content = HttpClientUtil.doGet(url);
            content = "\t\t\t\t\t" + content.replaceAll("\n", "\n\t\t\t\t\t");
            //将取来的章节放入缓存中
            saveChapterContentInRedis(bookId, chapterId, content);
        }
        //从缓存中取章节的详情
        ChapterInfo chapterInfo = getChapterInfoByRedis(bookId, chapterId);
        //如果缓存取不到,那么去MySQL中取
        if (chapterInfo == null) {
            chapterInfo = chapterInfoMapper.selectByBookIdAndChapterId(bookId, chapterId);
            savaChapterInfoInRedis(bookId, chapterId, chapterInfo);
        }
        map.put("chapterInfo", chapterInfo);
        map.put("content", content);
        return map;
    }

    /**
     * 根据书籍的id获得图书详情
     *
     * @param bookId
     * @return
     */
    @Override
    public BookInfo getBookInfoByBookId(Integer bookId) {
        BookInfo bookInfo = null;
        //判断缓存中是否有书的详情
        String bookStr = (String) redisService.get(bookId + "");
        //如果存在直接从缓存取,否则去MySQL取
        if (StringUtils.isNotEmpty(bookStr)) {
            System.out.println(bookStr);
            JSONObject object = JSONObject.fromObject(bookStr);
            bookInfo = (BookInfo) JSONObject.toBean(object, BookInfo.class);
        } else {
            bookInfo = bookInfoMapper.selectByBookId(bookId);
            System.out.println(bookInfo.getIsVIP());
            if (bookInfo != null) {
                bookInfo.setPicUrl(ApiConstant.Config.IMGPATH + bookInfo.getPicUrl());
                int vipBook = checkVIPBookByBookId(bookId);
                bookInfo.setIsVIP(vipBook);
                String infoStr = JSONObject.fromObject(bookInfo).toString();
                redisService.set(bookId + "", infoStr, ApiConstant.Timer.ONE_DAY);
            }
        }
        return bookInfo;
    }

    /**
     * 分页显示章节列表
     *
     * @param bookId
     * @return
     */
    @Override
    public PageResult<ChapterInfo> getChapterListByBookId(Integer bookId, int pageIndex, int pageSize, int orderBy) {
        PageResult<ChapterInfo> pageResult = new PageResult<>();
        List<ChapterInfo> chapterInfos = new ArrayList<>();
        //从缓存中获取章节列表
        String isExists = (String) redisService.hmGet(bookId + "chapter" + orderBy, pageIndex + "_" + pageSize);
        if (isExists == null || "".equals(isExists) || isExists.length() < 5) {
            int offset = (pageIndex - 1) * pageSize;
            chapterInfos = chapterInfoMapper.selectListByBookIdForPage(bookId, offset, pageSize, orderBy);
            //将章节列表放入缓存中
            String listStr = JSONArray.fromObject(chapterInfos).toString();
            redisService.hmSet(bookId + "chapter" + orderBy, pageIndex + "_" + pageSize, listStr);
        } else {
            //直接从缓存中取
            JSONArray array = JSONArray.fromObject(isExists);
            chapterInfos = (List<ChapterInfo>) JSONArray.toCollection(array, ChapterInfo.class);
        }
        pageResult.setPageList(chapterInfos);
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);
        int total = chapterInfoMapper.countByBookId(bookId);
        pageResult.setTotal(total);
        return pageResult;
    }

    /**
     * 判断该书籍是否为VIP书籍
     *
     * @param bookId
     * @return
     */
    @Override
    public int checkVIPBookByBookId(Integer bookId) {
        Integer vipBook = bookInfoMapper.isVIPBook(bookId);
        if (vipBook != null && vipBook != 0) {
            return 1;
        }
        return 0;
    }

    /**
     * 阅读和消费接口
     *
     * @param bookId
     * @param chapterId
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> readBook(Integer bookId, Integer chapterId, String userId) {
        //先判断用户类型,如果包年用户直接阅读
        UserInfo userInfo = userInfoService.getUserInfoByLocal(userId);
        int userType = userInfo.getType();
        //包年看所有
        if (userType == ApiConstant.UserType.VIP_YEAR_USER) {
            return getChapterInfo(bookId, chapterId);
        }
        //如果用户是包月或者包季VIP
        if (userType == ApiConstant.UserType.VIP_MONTH_USER || userType == ApiConstant.UserType.VIP_QUARTER_USER) {
            int isVIPBook = checkVIPBookByBookId(bookId);
            if (isVIPBook == ApiConstant.BookType.VIP_BOOK) {
                return getChapterInfo(bookId, chapterId);
            } else {
                //付费阅读
                return payRead(bookId, chapterId, userInfo);
            }
        }
        //普通用户阅币阅读
        if (userType == ApiConstant.UserType.GENNERAL_USER) {
            return payRead(bookId, chapterId, userInfo);
        }
        return null;
    }

    /**
     * 关键字搜索图书
     *
     * @param keyWords
     * @return
     */
    @Override
    public List<BookInfo> searchBook(String keyWords, int pageIndex, int pageSize) {
        List<BookInfo> bookInfos = new ArrayList<>();
        //先从关键字库中查询
        bookInfos.addAll(bookInfoMapper.selectBooksLikeKeyWords(keyWords));
        //再根据书名相似查
        bookInfos.addAll(bookInfoMapper.selectBooksLikeBookName(keyWords));
        List<BookInfo> books = new ArrayList<>();
        pageSize = bookInfos.size() < pageSize ? bookInfos.size() : pageSize;
        for (int i = (pageIndex - 1) * pageSize; i < pageSize; i++) {
            int vipBook = checkVIPBookByBookId(bookInfos.get(i).getBookId());
            bookInfos.get(i).setIsVIP(vipBook);
            bookInfos.get(i).setPicUrl(ApiConstant.Config.IMGPATH + bookInfos.get(i).getPicUrl());
            books.add(bookInfos.get(i));
        }
        return books;
    }

    @Override
    public PageResult<BookInfo> recommendBook(String author, String category, int pageIndex, int pageSize) {
        PageResult<BookInfo> pageResult = new PageResult<>();
        List<BookInfo> bookInfos = new ArrayList<>();
        List<BookInfo> authorBooks = null;
        //从缓存中取交叉推荐的内容
        String isExists = (String) redisService.get("recommend" + author + "_" + category);
        if (isExists == null || "".equals(isExists) || isExists.length() < 5) {
            //不存在的话从MYSQL中取
            if (author != null) {
                authorBooks = bookInfoMapper.selectBooksByAuthor(author);
            }
            List<BookInfo> categoryBooks = bookInfoMapper.selectBooksByCategory(category);
            authorBooks.addAll(categoryBooks);
            System.out.println(authorBooks);
            //将取得的书籍放入缓存
            if (authorBooks != null && authorBooks.size() > 0) {
                String arrayStr = JSONArray.fromObject(authorBooks).toString();
                redisService.set(arrayStr, ApiConstant.Timer.THREE_DAY);
            }
        } else {
            //如果缓存中可以取到,就直接从缓存中取
            JSONArray array = JSONArray.fromObject(isExists);
            authorBooks = (List<BookInfo>) JSONArray.toCollection(array, BookInfo.class);
        }

        if (authorBooks != null && authorBooks.size() > 0) {
            int offset = (pageIndex - 1) * pageSize;
            int total = authorBooks.size();
            pageSize = authorBooks.size() < pageSize ? authorBooks.size() : pageSize;
            for (int i = offset; i < pageSize; i++) {
                int vipBook = checkVIPBookByBookId(authorBooks.get(i).getBookId());
                authorBooks.get(i).setIsVIP(vipBook);
                authorBooks.get(i).setPicUrl(ApiConstant.Config.IMGPATH + authorBooks.get(i).getPicUrl());
                bookInfos.add(authorBooks.get(i));
            }
            pageResult.setPageIndex(pageIndex);
            pageResult.setPageSize(pageSize);
            pageResult.setTotal(total);
            pageResult.setPageList(bookInfos);
        }
        return pageResult;
    }

    /**
     * 书币购买章节,付费阅读
     *
     * @param bookId
     * @param chapterId
     * @param userInfo
     */
    @Transactional
    private Map<String, Object> payRead(Integer bookId, Integer chapterId, UserInfo userInfo) {
        Map<String, Object> chapterInfo = getChapterInfo(bookId, chapterId);
        ChapterInfo chapter = (ChapterInfo) chapterInfo.get("chapterInfo");
        //免费章节直接看
        if (chapter.getIsFree() == 1) {
            return chapterInfo;
        }
        //如果购买过的章节,可以直接看
        UserAssetExample example = new UserAssetExample();
        example.createCriteria().andUserIdEqualTo(userInfo.getUserId()).andBookIdEqualTo(bookId).andChapterIdEqualTo(chapterId);
        List<UserAsset> userAssets = userAssetMapper.selectByExample(example);
        if (userAssets != null && userAssets.size() > 0) {
            return chapterInfo;
        }
        //阅读章节的价格
        int kWord = chapter.getWords() % 1000 == 0 ? chapter.getWords() / 1000 : chapter.getWords() / 1000 + 1;
        int chapterPrice = kWord * ApiConstant.Price.UNIT_PRICE;
        //根据用户的等级进行折扣
        UserEmpiricalExample empiricalExample = new UserEmpiricalExample();
        empiricalExample.createCriteria().andUserIdEqualTo(userInfo.getUserId());
        UserEmpirical empirical = userEmpiricalMapper.selectByExample(empiricalExample).get(0);
        //打折后的价格
        int discountPrice = (int) Math.round(chapterPrice * empirical.getUserDiscount());
        //判断用户的阅币是否够购买本章
        boolean isEnough = userInfo.getCoin() - discountPrice >= 0 ? true : false;
        chapterInfo.put("chapterprice", chapterPrice);
        chapterInfo.put("discountprice", discountPrice);
        if (isEnough == false) {
            chapterInfo.put("content", "您的阅币余额不足,请充值!");
            return chapterInfo;
        }
        //书币充足,解锁本章
        userInfo.setCoin(userInfo.getCoin() - discountPrice);
        String objectStr = JSONObject.fromObject(userInfo).toString();
        redisService.hmSet("userInfo", userInfo.getUserId(), objectStr);
        //将用户阅读了该章节存入用户资产中
        UserAsset asset = new UserAsset();
        asset.setAmount(discountPrice);
        asset.setBookId(bookId);
        asset.setChapterId(chapterId);
        asset.setUserId(userInfo.getUserId());
        asset.setCreateTime(new Date());
        userAssetMapper.insertSelective(asset);
        return chapterInfo;
    }

    /**
     * 将MySQL取得的值放入缓存
     *
     * @param bookId
     * @param chapterId
     * @param chapterInfo
     */
    private void savaChapterInfoInRedis(Integer bookId, Integer chapterId, ChapterInfo chapterInfo) {
        String getChapterInfoKey = RedisKey.getChapterInfoKey(bookId, chapterId);
        String chapterStr = JSONObject.fromObject(chapterInfo).toString();
        redisService.set(getChapterInfoKey, chapterStr, ApiConstant.Timer.ONE_DAY);
    }

    /**
     * 将OSS取得的章节详情放入缓存
     *
     * @param bookId
     * @param chapterId
     * @param content
     */
    private void saveChapterContentInRedis(Integer bookId, Integer chapterId, String content) {
        String getChapterContentKey = RedisKey.getChapterContentKey(bookId, chapterId);
        redisService.set(getChapterContentKey, content, ApiConstant.Timer.ONE_DAY);
    }

    /**
     * 从OSS中获取下载地址
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    private String download(String bookId, String chapterId) {
        String url = fileUploadService.getObjectUrl(OSSCacheKey.buildDownloadChapterKey(bookId, chapterId), ApiConstant.FileType.CHAPTER);
        if (StringUtils.isNotEmpty(url) && url.indexOf("http://") > -1) {
            url = url.replace("http://", "https://");
        }
        return url;
    }

    /**
     * 从缓存中取章节详情
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    private ChapterInfo getChapterInfoByRedis(Integer bookId, Integer chapterId) {
        String getChapterInfoKey = RedisKey.getChapterInfoKey(bookId, chapterId);
        String chapterStr = (String) redisService.get(getChapterInfoKey);
        JSONObject jsonObject = JSONObject.fromObject(chapterStr);
        ChapterInfo chapterInfo = (ChapterInfo) JSONObject.toBean(jsonObject, ChapterInfo.class);
        return chapterInfo;
    }

    /**
     * 从缓存中获得书籍章节的内容
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    private String getChapterContentByRedis(Integer bookId, Integer chapterId) {
        String getChapterContentKey = RedisKey.getChapterContentKey(bookId, chapterId);
        String content = "";
        String value = (String) redisService.get(getChapterContentKey);
        if (StringUtils.isNotEmpty(value)) {
            content = value;
        }
        return content;
    }

}
