package com.jxtc.bookapp.controller;

import com.aliyun.oss.model.OSSObjectSummary;
import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.FileUploadResult;
import com.jxtc.bookapp.service.FileUploadService;
import groovy.io.FileType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/jxapp/file")
@Api(value = "文件上传接口", tags = "文件上传接口")
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;

    @ApiOperation(value = "头像上传和修改", notes = "头像上传和修改", httpMethod = "POST")
    @RequestMapping(value = "imgupload", method = RequestMethod.POST)
    @ResponseBody
    public FileUploadResult upload(@RequestParam("file") MultipartFile uploadFile, @ApiParam(value = "用户id", required = false) @RequestParam(value = "userId", defaultValue = "", required = false) String userId)
            throws Exception {
        return this.fileUploadService.upload(uploadFile, ApiConstant.FileType.IMG, userId);
    }


   /* @RequestMapping("file/delete")
    @ResponseBody
    public FileUploadResult delete(@RequestParam("fileName") String objectName, String fileType)
            throws Exception {
        return this.fileUploadService.delete(objectName, fileType);
    }*/


    /**
     * @return
     * @author lastwhisper
     * @desc 根据文件名下载oss上的文件
     * @Param objectName
     */
   /* @RequestMapping(value = "download",method = RequestMethod.GET)
    @ResponseBody
    public void download(@RequestParam("fileName") String objectName, HttpServletResponse response, String fileType) throws IOException {
        //通知浏览器以附件形式下载
        response.setHeader("Content-Disposition",
                "attachment;filename=" + new String(objectName.getBytes(), "ISO-8859-1"));
        this.fileUploadService.exportOssFile(response.getOutputStream(), objectName, fileType);
    }*/
}