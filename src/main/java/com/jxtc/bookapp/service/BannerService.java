package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Banner;
import com.jxtc.bookapp.entity.BannerImgs;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerService {



    /**
     * 添加banner位
     *
     * @param banner
     */
    void insertBanner(Banner banner);


    /**
     * 查看所有推荐为的banner
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<Banner> getBannerList(int pageIndex, int pageSize);

    /**
     * 删除banner位
     *
     * @param id
     */
    @Transactional
    void deleteBanner(int id);

    /**
     * 通过id找到对应的Banner对象
     *
     * @param id
     * @return
     */
    Banner findBannerById(int id);

    /**
     * 像banner推荐位添加banner图
     *
     * @param bannerImgs
     */
    void insertBannerImgToBanner(BannerImgs bannerImgs);


    /**
     * 删除banner推荐位的banner图
     *
     * @param id
     */
    void deleteBannerImgToBanner(int id);


    /**
     * 获得推荐为的轮播图
     *
     * @param bannerId
     * @return
     */
    List<BannerImgs> getBannerImageByBannerId(int bannerId);
}