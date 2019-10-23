package com.jxtc.bookapp.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.jxtc.bookapp.config.AliyunOSSConfig;
import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.OSSCacheKey;
import com.jxtc.bookapp.config.RedisKey;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.app.UserAssetMapper;
import com.jxtc.bookapp.mapper.app.UserCoinMapper;
import com.jxtc.bookapp.mapper.app.UserEmpiricalMapper;
import com.jxtc.bookapp.mapper.app.UserInfoMapper;
import com.jxtc.bookapp.mapper.book.BookInfoMapper;
import com.jxtc.bookapp.mapper.book.ChapterInfoMapper;
import com.jxtc.bookapp.service.*;
import com.jxtc.bookapp.utils.HttpClientUtil;
import com.jxtc.bookapp.utils.PageResult;
import com.jxtc.bookapp.utils.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 图书和章节服务
 */
@Service(value = "bookInfoService")
public class BookInfoServiceImpl implements BookInfoService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
    @Autowired
    private ConsumeService consumeService;
    @Autowired
    private UserCoinMapper userCoinMapper;
    @Autowired
    private UserEmpiricalService userEmpiricalService;
    @Autowired
    private AliyunOSSConfig aliyunOSSConfig;
    @Autowired
    private SearchKeywordsService searchKeywordsService;

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
            logger.info(bookStr);
            JSONObject object = JSONObject.fromObject(bookStr);
            bookInfo = (BookInfo) JSONObject.toBean(object, BookInfo.class);
        } else {
            bookInfo = bookInfoMapper.selectByBookId(bookId);
            logger.info("书籍类型", bookInfo.getIsVIP());
            if (bookInfo != null) {
                bookInfo.setPicUrl(ApiConstant.Config.IMGPATH + bookInfo.getPicUrl());
                int vipBook = checkVIPBookByBookId(bookId);
                bookInfo.setIsVIP(vipBook);
                String infoStr = JSONObject.fromObject(bookInfo).toString();
                redisService.set(bookId + "_BOOKINFO", infoStr, ApiConstant.Timer.ONE_DAY);
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
        String isExists = (String) redisService.get(bookId + "_chapter_" + orderBy + "_" + pageIndex + "_" + pageSize);
        if (isExists == null || "".equals(isExists) || isExists.length() < 5) {
            int offset = (pageIndex - 1) * pageSize;
            chapterInfos = chapterInfoMapper.selectListByBookIdForPage(bookId, offset, pageSize, orderBy);
            //将章节列表放入缓存中
            String listStr = JSONArray.fromObject(chapterInfos).toString();
            redisService.set(bookId + "_chapter_" + orderBy + "_" + pageIndex + "_" + pageSize, listStr, ApiConstant.Timer.ONE_DAY);
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
        Map<String, Object> map = getChapterInfo(bookId, chapterId);
        ChapterInfo chapterInfo = (ChapterInfo) map.get("chapterInfo");
        if (StringUtils.isEmpty(userId)) {
            //如果为未登录状态
            logger.info("用户未登录状态");
            if (chapterInfo.getIsFree() != 1) {
                map.put("content", "您还没有登录,登录即可阅读更多章节!");
            }
            return map;
        }
        //先判断用户类型,如果包年用户直接阅读
        UserInfo userInfo = userInfoService.getUserInfoByLocal(userId);
        String isUpdate = (String) redisService.get(TimeUtil.getYearMonthDay(new Date()) + "_update_" + userId);
        if (StringUtils.isEmpty(isUpdate)) {
            //修改用户的活动时间
            userInfoMapper.updateUserUpdateTime(userId, new Date());
            redisService.set(TimeUtil.getYearMonthDay(new Date()) + "_update_" + userId, "true", ApiConstant.Timer.ONE_DAY);
        }
        //迎新活动,新用户三天免费阅读
        Date createTime = userInfo.getCreateTime();
        if (new Date().getTime() - createTime.getTime() <= ApiConstant.Timer.ONE_DAY_MSEC) {
            logger.info("新用户注册,一天限免阅读");
            chapterInfo.setIsFree((byte) 1);
            map.put("chapterInfo", chapterInfo);
            return map;
        }
        int userType = userInfo.getType();
        //包年看所有
        if (userType == ApiConstant.UserType.VIP_YEAR_USER) {
            logger.info("包年看所有");
            chapterInfo.setIsFree((byte) 1);
            map.put("chapterInfo", chapterInfo);
            return map;
        }
        //如果用户是包月或者包季VIP
        if (userType == ApiConstant.UserType.VIP_MONTH_USER || userType == ApiConstant.UserType.VIP_QUARTER_USER) {
            logger.info("用户是包月或者包季用户");
            int isVIPBook = checkVIPBookByBookId(bookId);
            if (isVIPBook == ApiConstant.BookType.VIP_BOOK) {
                logger.info("vip用户看了VIP书");
                return getChapterInfo(bookId, chapterId);
            } else {
                //付费阅读
                logger.info("vip用户看了普通书");
                return payRead(bookId, chapterId, userInfo);
            }
        }
        //普通用户阅币阅读
        if (userType == ApiConstant.UserType.GENNERAL_USER) {
            logger.info("普通用户看书");
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
        System.out.println(keyWords);
        List<BookInfo> bookInfos = new ArrayList<>();
        //先从关键字库中查询
        bookInfos.addAll(bookInfoMapper.selectBooksLikeKeyWords(keyWords));
        //再根据书名相似查
        bookInfos.addAll(bookInfoMapper.selectBooksLikeBookName(keyWords));
        //搜索简介相似查
        bookInfos.addAll(bookInfoMapper.selectBooksByDes(keyWords));
        List<BookInfo> books = new ArrayList<>();
        pageSize = bookInfos.size() < pageSize ? bookInfos.size() : pageSize;
        for (int i = (pageIndex - 1) * pageSize; i < pageSize; i++) {
            int vipBook = checkVIPBookByBookId(bookInfos.get(i).getBookId());
            bookInfos.get(i).setIsVIP(vipBook);
            bookInfos.get(i).setPicUrl(ApiConstant.Config.IMGPATH + bookInfos.get(i).getPicUrl());
            books.add(bookInfos.get(i));
        }
        //记录用户搜索的关键词
        searchKeywordsService.addKeyWords(keyWords);
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
            logger.info("推荐书籍列表", authorBooks);
            //将取得的书籍放入缓存
            if (authorBooks != null && authorBooks.size() > 0) {
                String arrayStr = JSONArray.fromObject(authorBooks).toString();
                redisService.set("recommend" + author + "_" + category, arrayStr, ApiConstant.Timer.THREE_DAY);
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

    @Override
    public PageResult<BookInfo> getBookInfoListByParam(Integer bookId, String bookName, String author, Integer status, int pageIndex, int pageSize) {
        PageResult<BookInfo> pageResult = new PageResult<>();
        pageResult.setPageIndex(pageIndex);
        pageResult.setPageSize(pageSize);
        System.out.println("bookMapper是" + bookInfoMapper);
        int total = bookInfoMapper.countBooksByParams(bookId, bookName, author, status);
        pageResult.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<BookInfo> bookInfos = bookInfoMapper.selectBooksByParams(bookId, bookName, author, status, offset, pageSize);
        pageResult.setPageList(bookInfos);
        return pageResult;
    }

    @Override
    public Map<String, Object> getChapterInfoForH5(int bookId, int chapterId) {
        Map<String, Object> map = new HashMap<>();
        //先从缓存中取章节的详情
        String content = (String) redisService.get("H5chapterContent_" + bookId + "_" + chapterId);
        //如果取不到就去OSS中取
        if (StringUtils.isEmpty(content)) {
            String url = download(bookId + "", chapterId + "");
            String result = HttpClientUtil.doGet(url);
            StringBuilder sb = new StringBuilder();
            content = sb.append(result.replace("\r\n", "</p><p>").replace("\n", "</p><p>"))
                    .append("<p>").toString();
            //保存到redis中
            redisService.set("H5chapterContent_" + bookId + "_" + chapterId, content);
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

    @Override
    public void updateBookInfo(BookInfo bookInfo) {
        bookInfoMapper.updateByPrimaryKeySelective(bookInfo);
        redisService.remove("*bangdan");
        redisService.remove(bookInfo.getBookId() + "_BOOKINFO");
    }

    @Override
    public String uploadBookPic(String bookId, MultipartFile file) {
        if (file == null) {
            return "文件为空请重新选择";
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String ENDPOINT = aliyunOSSConfig.getEndpoint();
        final String ACCESSKEYID = aliyunOSSConfig.getAccessKeyId();
        final String ACCESSKEYSECRET = aliyunOSSConfig.getAccessKeySecret();
        final String BUCKETNAME = "jxxs-pic";
        String url = null;
        String key = "pic/" + bookId + "/" + bookId + ".jpg";
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
        try {
            // 带进度条的上传
            ossClient.putObject(new PutObjectRequest(BUCKETNAME, key, inputStream));
        } catch (OSSException oe) {
            oe.printStackTrace();
            key = null;
        } catch (ClientException ce) {
            ce.printStackTrace();
            key = null;
        } catch (Exception e) {
            e.printStackTrace();
            key = null;
        } finally {
            ossClient.shutdown();
        }
        if (key != null) {
            // 拼接文件访问路径。由于拼接的字符串大多为String对象，而不是""的形式，所以直接用+拼接的方式没有优势
            StringBuffer sb = new StringBuffer();
            sb.append("http://").append(BUCKETNAME).append(".").append(ENDPOINT
            ).append("/").append(key);
            url = sb.toString();
        }
        logger.info("图片上传成功" + url);
        return key;
    }

    @Override
    public void updateChapterContent(int bookId, int chapterId, String content) {
        saveChapterContentInRedis(bookId, chapterId, content);
        redisService.set("H5chapterContent_" + bookId + "_" + chapterId, content);
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
            logger.info("免费章节直接看");
            return chapterInfo;
        }
        //如果购买过的章节,可以直接看
        Integer tableIndex = getTableIndex(userInfo.getUserId());
        UserAsset asset = userAssetMapper.selectByUserIdBookIdAndChapterId(userInfo.getUserId(), bookId, chapterId, tableIndex);
        if (asset != null) {
            logger.info("用户资产里面有这个章节直接看");
            return chapterInfo;
        }
        //阅读章节的价格
        int kWord = chapter.getWords() % 1000 == 0 ? chapter.getWords() / 1000 : chapter.getWords() / 1000 + 1;
        int chapterPrice = kWord * ApiConstant.Price.UNIT_PRICE;
        //根据用户的等级进行折扣
        UserEmpirical empirical = userEmpiricalService.findEmpiricalByUserId(userInfo.getUserId());
        //打折后的价格
        int discountPrice = (int) Math.round(chapterPrice * empirical.getUserDiscount());
        //判断用户的阅币是否够购买本章
        //获得用户的阅币
        int coin = userCoinMapper.getCoinByUserId(userInfo.getUserId());
        boolean isEnough = coin - discountPrice >= 0 ? true : false;
        chapterInfo.put("chapterprice", chapterPrice);
        chapterInfo.put("discountprice", discountPrice);
        if (isEnough == false) {
            logger.info("用户阅币不足");
            chapterInfo.put("content", "您的阅币余额不足,请充值!");
            return chapterInfo;
        }
        //书币充足,解锁本章
        userCoinMapper.addCoinByUserId(userInfo.getUserId(), -discountPrice);
        logger.info("书币充足,解锁本章");
        //统计消费
        consumeService.addConsume(userInfo.getUserId(), bookId, chapterId, discountPrice);
        //将用户阅读了该章节存入用户资产中
        UserAsset assetNew = new UserAsset();
        assetNew.setAmount(discountPrice);
        assetNew.setBookId(bookId);
        assetNew.setChapterId(chapterId);
        assetNew.setUserId(userInfo.getUserId());
        assetNew.setCreateTime(new Date());
        assetNew.setTableIndex(tableIndex);
        userAssetMapper.insertSelective(assetNew);
        logger.info("将用户阅读了该章节存入用户资产中");
        return chapterInfo;
    }

    /**
     * 根据用户ID的最后一位决定将用户资产保存到不同的表中
     *
     * @param userId
     * @return
     */
    private Integer getTableIndex(String userId) {
        Integer tableIndex = Integer.valueOf(userId.substring(userId.length() - 1));
        return tableIndex;
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
        redisService.set(getChapterInfoKey, chapterStr);
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
        redisService.set(getChapterContentKey, content);
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
