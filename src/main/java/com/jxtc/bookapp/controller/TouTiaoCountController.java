package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.entity.ToutiaoCount;
import com.jxtc.bookapp.service.ToutiaoCountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Api(value = "头条统计", tags = "头条统计")
@Controller
public class TouTiaoCountController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ToutiaoCountService toutiaoCountService;

    @RequestMapping(value = "toutiao", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "头条统计接口", notes = "头条统计接口")
    @ResponseBody
    public String touTiaoCountUrl(@ApiParam(value = "广告计划id", required = false, defaultValue = "__AID__")
                                  @RequestParam(value = "adid", required = false, defaultValue = "__AID__") String adid,
                                  @ApiParam(value = "广告创意id", required = false, defaultValue = "__CID__")
                                  @RequestParam(value = "cid", required = false, defaultValue = "__CID__") String cid,
                                  @ApiParam(value = "用户终端的15位数字IMEI", required = false, defaultValue = "__IMEI__")
                                  @RequestParam(value = "imei", required = false, defaultValue = "__IMEI__") String imei,
                                  @ApiParam(value = "MAC地址", required = false, defaultValue = "__MAC__")
                                  @RequestParam(value = "mac", required = false, defaultValue = "__MAC__") String mac,
                                  @ApiParam(value = "手机android_id", required = false, defaultValue = "__AID__")
                                  @RequestParam(value = "androidid", required = false, defaultValue = "__AID__") String androidid,
                                  @ApiParam(value = "客户端操作系统", required = false, defaultValue = "__OS__")
                                  @RequestParam(value = "os", required = false, defaultValue = "__OS__") String os,
                                  @ApiParam(value = "时间戳", required = false, defaultValue = "__TS__")
                                  @RequestParam(value = "timestamp", required = false, defaultValue = "__TS__") String timestamp,
                                  @ApiParam(value = "回调接口地址", required = false, defaultValue = "__CALLBACK_URL__")
                                  @RequestParam(value = "callback_url", required = false, defaultValue = "__CALLBACK_URL__") String callback_url) {
        logger.info("广告计划id:" + adid);
        logger.info("广告创意id:" + cid);
        logger.info("用户终端的15位数字IMEI:" + imei);
        logger.info("MAC地址:" + mac);
        logger.info("手机android_id:" + androidid);
        logger.info("客户端操作系统:" + os);
        logger.info("时间戳:" + timestamp);
        logger.info("回调接口地址:" + callback_url);
        ToutiaoCount toutiaoCount = new ToutiaoCount();
        toutiaoCount.setAdid(adid);
        toutiaoCount.setCdid(cid);
        toutiaoCount.setImei(imei);
        toutiaoCount.setMac(mac);
        toutiaoCount.setAndroidid(androidid);
        toutiaoCount.setOs(os);
        toutiaoCount.setTimestamp(timestamp);
        toutiaoCount.setCallbackUrl(callback_url);
        toutiaoCountService.addTaotiaoCount(toutiaoCount);
        return "success";
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String toIndex() {
        return "index";
    }
}
