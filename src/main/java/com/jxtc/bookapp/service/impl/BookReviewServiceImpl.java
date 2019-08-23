package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.entity.BookInfo;
import com.jxtc.bookapp.entity.BookReview;
import com.jxtc.bookapp.entity.BookReviewExample;
import com.jxtc.bookapp.entity.UserInfo;
import com.jxtc.bookapp.mapper.app.BookReviewMapper;
import com.jxtc.bookapp.service.BookReviewService;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.service.UserInfoService;
import com.jxtc.bookapp.utils.JSONUtil;
import com.jxtc.bookapp.utils.PageResult;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookReviewServiceImpl implements BookReviewService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BookReviewMapper bookReviewMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void insert(BookReview bookReview) {
        bookReview.setCreateTime(new Date());
        bookReview.setPraise(0);
        redisService.remove(bookReview.getBookId() + "review");
        redisService.remove(bookReview.getUserId() + "review");
        bookReviewMapper.insertSelective(bookReview);
    }

    @Override
    public PageResult<BookReview> getBookReviewList(String userId, int pageIndex, int pageSize, int bookId) {
        PageResult<BookReview> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        BookReviewExample example = new BookReviewExample();
        example.createCriteria().andBookIdEqualTo(bookId);
        int total = bookReviewMapper.countByExample(example);
        page.setTotal(total);
        List<BookReview> bookReviews = new ArrayList<>();
        //先从缓存中取
        String isExists = (String) redisService.hmGet(bookId + "review", pageIndex + "_" + pageSize);
        if (isExists == null || "".equals(isExists) || isExists.length() < 5) {
            bookReviews = bookReviewMapper.selectPageBookReviews(bookId, (pageIndex - 1) * pageSize, pageSize);
            if (bookReviews != null && bookReviews.size() > 0) {
                String arrayStr = JSONUtil.listToJsonStr(bookReviews);
                logger.info("书评来自MYSQL");
                redisService.hmSet(bookId + "review", pageIndex + "_" + pageSize, arrayStr);
            }
        } else {
            JSONArray array = JSONArray.fromObject(isExists);
            bookReviews = (List<BookReview>) JSONArray.toCollection(array, BookReview.class);
            logger.info("书评来自缓存");
        }
        if (bookReviews != null && bookReviews.size() > 0) {
            for (BookReview bookReview : bookReviews) {
                String flag = (String) redisService.get("PRAISE" + userId + "_" + bookReview.getId());
                if (StringUtils.isNotEmpty(flag)) {
                    bookReview.setIsPraise(1);
                } else {
                    bookReview.setIsPraise(0);
                }
            }
        }
        page.setPageList(bookReviews);
        return page;
    }

    @Override
    public BookReview getBookReview(String userId, int id) {
        BookReview review = bookReviewMapper.selectByPrimaryKey(id);
        //判断当前用户是否给该条书评点过赞
        String flag = (String) redisService.get("PRAISE" + userId + "_" + review.getId());
        if (StringUtils.isNotEmpty(flag)) {
            review.setIsPraise(1);
        } else {
            review.setIsPraise(0);
        }
        UserInfo userInfo = userInfoService.getUserInfoByLocal(review.getUserId());
        review.setHeadImg(userInfo.getHeadimgurl());
        review.setNickname(userInfo.getNickname());
        return review;
    }

    @Override
    public PageResult<BookReview> getMyselfBookReview(int pageIndex, int pageSize, String userId) {
        PageResult<BookReview> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        BookReviewExample example = new BookReviewExample();
        example.createCriteria().andUserIdEqualTo(userId);
        int total = bookReviewMapper.countByExample(example);
        page.setTotal(total);
        List<BookReview> bookReviews = new ArrayList<>();
        //先从缓存中取
        String isExists = (String) redisService.hmGet(userId + "review", pageIndex + "_" + pageSize);
        if (isExists == null || "".equals(isExists) || isExists.length() < 5) {
            bookReviews = bookReviewMapper.selectPageUserBookReviews(userId, (pageIndex - 1) * pageSize, pageSize);
            if (bookReviews != null && bookReviews.size() > 0) {
                String arrayStr = JSONUtil.listToJsonStr(bookReviews);
                redisService.hmSet(userId + "review", pageIndex + "_" + pageSize, arrayStr);
            }
        } else {
            JSONArray array = JSONArray.fromObject(isExists);
            bookReviews = (List<BookReview>) JSONArray.toCollection(array, BookReview.class);
        }
        if (bookReviews != null && bookReviews.size() > 0) {
            for (BookReview bookReview : bookReviews) {
                String bookPic = ApiConstant.Config.IMGPATH + "pic/" + bookReview.getBookId() + "/" + bookReview.getBookId() + ".jpg";
                bookReview.setHeadImg(bookPic);
                String flag = (String) redisService.get("PRAISE" + userId + "_" + bookReview.getId());
                if (StringUtils.isNotEmpty(flag)) {
                    bookReview.setIsPraise(1);
                } else {
                    bookReview.setIsPraise(0);
                }
            }
        }
        page.setPageList(bookReviews);
        return page;
    }

    @Override
    public void deleteMyselfBookReview(int id) {
        BookReview bookReview = bookReviewMapper.selectByPrimaryKey(id);
        redisService.remove(bookReview.getBookId() + "review");
        redisService.remove(bookReview.getUserId() + "review");
        bookReviewMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void praise(String userId, int id, int type) {
        BookReview bookReview = bookReviewMapper.selectByPrimaryKey(id);
        int praise = bookReview.getPraise() == null ? 0 : bookReview.getPraise();
        //如果是点赞
        if (type == ApiConstant.BookReviewPraiseType.LIKE) {
            //用户点赞完毕记录点赞的用户
            redisService.set("PRAISE" + userId + "_" + id, "true");
            praise = praise + 1;
        } else {
            //取消赞
            redisService.remove("PRAISE" + userId + "_" + id);
            praise = praise - 1;
        }
        redisService.remove(bookReview.getBookId() + "review");
        redisService.remove(bookReview.getUserId() + "review");
        bookReviewMapper.praise(id, praise);
    }

    @Override
    public PageResult<BookInfo> getHaveReviewBookList(String bookName, int pageIndex, int pageSize) {
        PageResult<BookInfo> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        int offset = (pageIndex - 1) * pageSize;
        List<BookInfo> reviewList = bookReviewMapper.selectHaveReviewBookList(bookName, offset, pageSize);
        if (reviewList != null && reviewList.size() > 0) {
            for (BookInfo bookInfo : reviewList) {
                int count = bookReviewMapper.countByBookId(bookInfo.getBookId());
                bookInfo.setHitsCount(count);
            }
        }
        int total = bookReviewMapper.countHaveReviewBook(bookName);
        page.setTotal(total);
        page.setPageList(reviewList);
        return page;
    }
}
