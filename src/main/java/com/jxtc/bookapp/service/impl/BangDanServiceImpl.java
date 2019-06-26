package com.jxtc.bookapp.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.BangdanBooksMapper;
import com.jxtc.bookapp.mapper.BangdanMapper;
import com.jxtc.bookapp.mapper.BookInfoMapper;
import com.jxtc.bookapp.mapper.MaterialMapper;
import com.jxtc.bookapp.service.BangDanService;
import com.jxtc.bookapp.service.BookInfoService;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.utils.PageResult;
import com.jxtc.bookapp.utils.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author wyl
 * 榜单书籍服务
 */
@Service
public class BangDanServiceImpl implements BangDanService {
    @Autowired
    private BangdanMapper bangdanMapper;
    @Autowired
    private BangdanBooksMapper bangdanBooksMapper;
    @Autowired
    private BookInfoMapper bookInfoMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BookInfoService bookInfoService;

    @Override
    public int insertBangDan(Bangdan bangdan) {
        return bangdanMapper.insert(bangdan);
    }

    @Override
    public PageResult<Bangdan> getBangDanList(int pageIndex, int pageSize) {
        PageResult<Bangdan> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        BangdanExample example = new BangdanExample();
        int total = bangdanMapper.countByExample(example);
        page.setTotal(total);
        List<Bangdan> list = bangdanMapper.selectBangDanList((pageIndex - 1) * pageSize, pageSize);
        page.setPageList(list);
        return page;
    }

    @Override
    public Bangdan findById(int id) {
        return bangdanMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateBangDan(Bangdan bangdan) {
        return bangdanMapper.updateByPrimaryKeySelective(bangdan);
    }

    @Override
    public int deleteBangDan(int id) {
        Bangdan bangdan = findById(id);
        BangdanBooksExample example = new BangdanBooksExample();
        example.createCriteria().andBangdanIdEqualTo(bangdan.getBangdanId());
        bangdanBooksMapper.deleteByExample(example);
        redisService.remove(bangdan.getBangdanId() + "bangdan");
        return bangdanMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertBangDanBooks(BangdanBooks bangdanBooks) {
        bangdanBooks.setCreateTime(new Date());
        bangdanBooks.setUpdateTime(new Date());
        redisService.remove(bangdanBooks.getBangdanId() + "bangdan");
        return bangdanBooksMapper.insertSelective(bangdanBooks);
    }

    @Override
    public void deleteBangDanBooks(int id) {
        BangdanBooks books = bangdanBooksMapper.selectByPrimaryKey(id);
        redisService.remove(books.getBangdanId() + "bangdan");
        bangdanBooksMapper.deleteByPrimaryKey(id);
    }


    @Override
    public PageResult<BangdanBooks> getBangDanBooks(int bangDanId, int pageIndex, int pageSize) {
        PageResult<BangdanBooks> page = new PageResult<>();
        List<BangdanBooks> list = new ArrayList<>();
        //从redis中取该页的内容
        String isExists = (String) redisService.hmGet(bangDanId + "bangdan", pageIndex + "_" + pageSize);
        //不存在从MySQL中取
        if (isExists == null || "".equals(isExists) || isExists.length() < 5) {
            int offset = (pageIndex - 1) * pageSize;
            list = bangdanBooksMapper.selectByBangDanId(bangDanId, offset, pageSize);
            if (list != null && list.size() > 0) {
                for (BangdanBooks book : list) {
                    BookInfo bookInfo = bookInfoMapper.selectByBookId(book.getBookId());
                    bookInfo.setIsVIP(bookInfoService.checkVIPBookByBookId(bookInfo.getBookId()));
                    bookInfo.setPicUrl(ApiConstant.Config.IMGPATH + bookInfo.getPicUrl());
                    book.setBookInfo(bookInfo);
                }
            }
            //将从MySQL中取出的内容放入缓存
            String listStr = JSONArray.fromObject(list).toString();
            System.out.println(listStr);
            redisService.hmSet(bangDanId + "bangdan", pageIndex + "_" + pageSize, listStr);
        } else {
            JSONArray array = JSONArray.fromObject(isExists);
            list = (List<BangdanBooks>) JSONArray.toCollection(array, BangdanBooks.class);
        }
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        BangdanBooksExample example = new BangdanBooksExample();
        example.createCriteria().andBangdanIdEqualTo(bangDanId);
        int total = bangdanBooksMapper.countByExample(example);
        page.setTotal(total);
        page.setPageList(list);
        return page;
    }

    @Override
    public List<Bangdan> getBangDanListByType(int type) {
        final int[] BOY = new int[]{157, 158, 159, 160};
        final int[] GRIL = new int[]{161, 162, 163, 164};
        final int[] CHUBAN = new int[]{165, 166, 167, 168};
        List<Bangdan> bangdans = new ArrayList<>();
        int[] flag = new int[4];
        switch (type) {
            case ApiConstant.BookBangDanType.BOY:
                flag = BOY;
                break;
            case ApiConstant.BookBangDanType.GRIL:
                flag = GRIL;
                break;
            case ApiConstant.BookBangDanType.CHUBAN:
                flag = CHUBAN;
                break;
        }
        for (int i : flag) {
            System.out.println(i);
            Bangdan bangdan = bangdanMapper.selectBandanByBangDanId(i);
            bangdans.add(bangdan);
        }
        return bangdans;
    }
}
