package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Banner;
import com.jxtc.bookapp.entity.BannerImgs;
import com.jxtc.bookapp.service.BannerService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "轮播图和推荐位接口", tags = "轮播图和推荐位接口")
@RequestMapping("/jxapp/banner")
@Controller
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation(value = "新建推荐位banner接口", notes = "新建推荐位banner接口", httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public JXResult addBanner(@ApiParam(value = "推荐位", required = false)
                              @RequestBody Banner banner) {
        bannerService.insertBanner(banner);
        return new JXResult(true, ApiConstant.StatusCode.OK, "添加成功");
    }

    @ApiOperation(value = "删除推荐位banner接口", notes = "删除推荐位banner接口", httpMethod = "DELETE")
    @ResponseBody
    @RequestMapping(value = "del", method = RequestMethod.DELETE)
    public JXResult deleteBanner(@ApiParam(value = "id", required = false)
                                 @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        bannerService.deleteBanner(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }

    @ApiOperation(value = "通过id查询推荐位banner接口", notes = "通过id查询推荐位banner接口", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "findbyid", method = RequestMethod.GET)
    public JXResult findBannerById(@ApiParam(value = "id", required = false)
                                   @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        Banner banner = bannerService.findBannerById(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", banner);
    }

    @ApiOperation(value = "查询推荐位banner列表接口", notes = "查询推荐位banner列表接口", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JXResult getBannerList(@ApiParam(value = "当前页", required = false)
                                  @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                  @ApiParam(value = "每页显示数量", required = false)
                                  @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        PageResult<Banner> banners = bannerService.getBannerList(pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", banners);
    }

    @ApiOperation(value = "根据推荐位banner的id获得推荐位中的banner图", notes = "根据推荐位banner的id获得推荐位中的banner图", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "list/banners", method = RequestMethod.GET)
    public JXResult getBannerList(@ApiParam(value = "推荐位id", required = false)
                                  @RequestParam(value = "bannerId", defaultValue = "1", required = false) int bannerId) {
        List<BannerImgs> BannerImgs = bannerService.getBannerImageByBannerId(bannerId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", BannerImgs);
    }

    @ApiOperation(value = "向某个推荐位banner添加banner图", notes = "向某个推荐位banner添加banner图", httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "add/banners", method = RequestMethod.POST)
    public JXResult addBannerImgs(@ApiParam(value = "推荐位图片", required = false)
                                  @RequestBody BannerImgs bannerImgs) {
        bannerService.insertBannerImgToBanner(bannerImgs);
        return new JXResult(true, ApiConstant.StatusCode.OK, "添加成功");
    }

    @ApiOperation(value = "删除推荐位banner中的一张banner图", notes = "删除推荐位banner中的一张banner图", httpMethod = "DELETE")
    @ResponseBody
    @RequestMapping(value = "del/banners", method = RequestMethod.DELETE)
    public JXResult delBangDanBooks(@ApiParam(value = "id", required = false)
                                    @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        bannerService.deleteBannerImgToBanner(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }

}
