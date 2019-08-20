package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Version;
import com.jxtc.bookapp.service.VersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "jxapp/version")
@Api(value = "app版本更新接口", tags = "app版本更新接口")
public class VersionController {
    @Autowired
    private VersionService versionService;

    @RequestMapping(value = "get/new", method = RequestMethod.GET)
    @ApiOperation(value = "获取最新的版本信息", notes = "获取最新的版本信息", httpMethod = "GET")
    public JXResult getNewVersion() {
        Version bestNewVersion = versionService.getBestNewVersion();
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bestNewVersion);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ApiOperation(value = "更新app的版本", notes = "更新app的版本", httpMethod = "POST")
    public JXResult updateAppVersion(@RequestParam(value = "file", required = true) MultipartFile file,
                                     @ApiParam(value = "版本号", defaultValue = "", required = false)
                                     @RequestParam(value = "versionId", required = false) String versionId,
                                     @ApiParam(value = "版本描述信息", defaultValue = "", required = false)
                                     @RequestParam(value = "des", required = false) String des) {
        Version version = new Version();
        version.setDes(des);
        version.setVersion(versionId);
        boolean flag = versionService.updateAppToOss(file, version);
        if (flag) {
            return new JXResult(true, ApiConstant.StatusCode.OK, "更新成功");
        }
        return new JXResult(false, ApiConstant.StatusCode.ERROR, "更新失败,请重试");
    }
}
