package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Material;
import com.jxtc.bookapp.service.MaterialService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api(value = "素材管理接口", tags = "素材管理接口")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @ApiOperation(value = "上传素材文件接口", notes = "上传素材文件接口", httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "upload/file", method = RequestMethod.POST)
    public JXResult uploadMaterialFile(@RequestParam("file") MultipartFile uploadFile) {
        String result = materialService.materialUpload(uploadFile);
        return new JXResult(true, ApiConstant.StatusCode.OK, "上传成功", result);
    }

    @ApiOperation(value = "上传轮播图素材接口", notes = "上传轮播图素材接口", httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "save/material", method = RequestMethod.POST)
    public JXResult uploadMaterial(@ApiParam(value = "素材地址", required = false)
                                   @RequestParam(value = "url", defaultValue = "", required = false) String url,
                                   @ApiParam(value = "素材名称", required = false)
                                   @RequestParam(value = "materialName", defaultValue = "", required = false) String materialName) {
        materialService.insertMaterialToMysql(url, materialName);
        return new JXResult(true, ApiConstant.StatusCode.OK, "保存成功");
    }

    @ApiOperation(value = "分页获取素材列表接口", notes = "分页获取素材列表接口", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "list/material", method = RequestMethod.GET)
    public JXResult getMaterialListByPage(@ApiParam(value = "当前页", required = false)
                                          @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                          @ApiParam(value = "每页显示数量", required = false)
                                          @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        PageResult<Material> materialByPage = materialService.getMaterialByPage(pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", materialByPage);
    }

    @ApiOperation(value = "批量删除素材接口", notes = "批量删除素材接口", httpMethod = "DELETE")
    @ResponseBody
    @RequestMapping(value = "del/material", method = RequestMethod.DELETE)
    public JXResult delMaterialsByIds(@ApiParam(value = "需要删除的素材Id数组", required = false)
                                      @RequestParam(value = "ids", defaultValue = "", required = false) int[] ids) {
        materialService.delMaterialById(ids);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }

    @ApiOperation(value = "根据名字搜索素材列表接口", notes = "根据名字搜索素材列表接口", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "search/material", method = RequestMethod.GET)
    public JXResult searchMaterialListByName(@ApiParam(value = "素材名", required = false)
                                             @RequestParam(value = "name", defaultValue = "", required = false) String name,
                                             @ApiParam(value = "当前页", required = false)
                                             @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                             @ApiParam(value = "每页显示数量", required = false)
                                             @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        PageResult<Material> materialPageResult = materialService.searchMaterials(name, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", materialPageResult);
    }
}
