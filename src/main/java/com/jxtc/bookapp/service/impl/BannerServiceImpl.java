package com.jxtc.bookapp.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.app.BannerImgsMapper;
import com.jxtc.bookapp.mapper.app.BannerMapper;
import com.jxtc.bookapp.mapper.app.MaterialMapper;
import com.jxtc.bookapp.service.BannerService;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.utils.PageResult;
import com.jxtc.bookapp.utils.TimeUtil;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private BannerMapper bannerMapper;
    @Autowired
    private BannerImgsMapper bannerImgsMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public void insertBanner(Banner banner) {
        bannerMapper.insertSelective(banner);
    }

    @Override
    public PageResult<Banner> getBannerList(int pageIndex, int pageSize) {
        PageResult<Banner> page = new PageResult<>();
        BannerExample example = new BannerExample();
        int total = bannerMapper.countByExample(example);
        page.setTotal(total);
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        List<Banner> bannerList = bannerMapper.selectBannerListByPage((pageIndex - 1) * pageSize, pageSize);
        page.setPageList(bannerList);
        return page;
    }

    @Override
    public void deleteBanner(int id) {
        Banner banner = bannerMapper.selectByPrimaryKey(id);
        if (banner == null) {
            return;
        }
        int bannerId = banner.getBannerId();
        //删除该推荐banner位中的所有图片
        BannerImgsExample example = new BannerImgsExample();
        example.createCriteria().andBannerIdEqualTo(bannerId);
        redisService.remove(bannerId + "banner");
        bannerImgsMapper.deleteByExample(example);
        bannerMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Banner findBannerById(int id) {
        return bannerMapper.selectByPrimaryKey(id);
    }

    @Override
    public void insertBannerImgToBanner(BannerImgs bannerImgs) {
        bannerImgs.setCreateTime(new Date());
        redisService.remove(bannerImgs.getBannerId() + "banner");
        bannerImgsMapper.insertSelective(bannerImgs);
    }

    @Override
    public void deleteBannerImgToBanner(int id) {
        BannerImgs bannerImgs = bannerImgsMapper.selectByPrimaryKey(id);
        redisService.remove(bannerImgs.getBannerId() + "banner");
        bannerImgsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<BannerImgs> getBannerImageByBannerId(int bannerId) {
        List<BannerImgs> bannerImgs = new ArrayList<>();
        String isExists = (String) redisService.get(bannerId + "banner");
        if (isExists == null || isExists.length() < 5 || "".equals(isExists)) {
            //缓存中不存在,从MySQL中取
            bannerImgs = bannerImgsMapper.selectBannerImgs(bannerId);
            if (bannerImgs != null && bannerImgs.size() > 0) {
                //放入缓存
                String bannerImgsStr = JSONArray.fromObject(bannerImgs).toString();
                redisService.set(bannerId + "banner", bannerImgsStr);
            }
        } else {
            //缓存中存在 从缓存中取
            JSONArray array = JSONArray.fromObject(isExists);
            bannerImgs = (List<BannerImgs>) JSONArray.toCollection(array, BannerImgs.class);
        }
        return bannerImgs;
    }


}
