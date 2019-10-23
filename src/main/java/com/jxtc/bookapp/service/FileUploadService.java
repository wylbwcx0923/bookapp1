package com.jxtc.bookapp.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.jxtc.bookapp.config.AliyunOSSConfig;
import com.jxtc.bookapp.config.FileUploadResult;
import com.jxtc.bookapp.entity.UserInfo;
import com.jxtc.bookapp.entity.UserInfoExample;
import com.jxtc.bookapp.mapper.app.UserInfoMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * @author wyl
 * 阿里云OSS文件操作服务
 */
@Service
public class FileUploadService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // 允许上传的格式
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg",
            ".jpeg", ".gif", ".png"};
    @Autowired
    private OSS ossClient;
    @Autowired
    private AliyunOSSConfig aliyunOSSConfig;
    @Autowired
    private UserInfoMapper userInfoMapper;

    private String bucketName = "";
    private String urlPrefix = "";

    /**
     * 文件上传
     *
     * @param uploadFile 上传的文件
     * @return
     */
    public FileUploadResult upload(MultipartFile uploadFile, String fileType, String userId) {
        // 校验图片格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(),
                    type)) {
                isLegal = true;
                break;
            }
        }
        //封装Result对象，并且将文件的byte数组放置到result对象中
        FileUploadResult fileUploadResult = new FileUploadResult();
        if (!isLegal) {
            fileUploadResult.setStatus("error");
            return fileUploadResult;
        }
        //文件新路径
        String fileName = uploadFile.getOriginalFilename();
        String filePath = getFilePath(fileName);
        // 上传到阿里云
        bucketName = fileType.equals("img") ? aliyunOSSConfig.getBucketNameImg() : aliyunOSSConfig.getBucketNameChapter();
        urlPrefix = fileType.equals("img") ? aliyunOSSConfig.getUrlPrefixImg() : aliyunOSSConfig.getUrlPrefixChapter();
        try {
            ossClient.putObject(bucketName, filePath, new
                    ByteArrayInputStream(uploadFile.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            //上传失败
            fileUploadResult.setStatus("error");
            return fileUploadResult;
        }
        fileUploadResult.setStatus("done");
        fileUploadResult.setResponse("success");
        //this.aliyunConfig.getUrlPrefix() + filePath 文件路径需要保存到数据库
        fileUploadResult.setName(urlPrefix + filePath);
        fileUploadResult.setUid(String.valueOf(System.currentTimeMillis()));
        //文件上传成功,更新用户头像
        UserInfoExample example = new UserInfoExample();
        example.createCriteria().andUserIdEqualTo(userId);
        UserInfo fileUser = new UserInfo();
        fileUser.setHeadimgurl(fileUploadResult.getName());
        userInfoMapper.updateByExampleSelective(fileUser, example);
        logger.info("文件上传结果", fileUploadResult);
        return fileUploadResult;
    }

    /**
     * 生成文件以及文件名
     *
     * @param sourceFileName 文件名
     * @return
     */
    private String getFilePath(String sourceFileName) {
        DateTime dateTime = new DateTime();
        return "images/" + dateTime.toString("yyyy")
                + "/" + dateTime.toString("MM") + "/"
                + dateTime.toString("dd") + "/" + System.currentTimeMillis() +
                RandomUtils.nextInt(9999) + 100 + "." +
                StringUtils.substringAfterLast(sourceFileName, ".");
    }

    /**
     * 获得文件列表
     *
     * @return
     */
    public List<OSSObjectSummary> list(String fileType) {
        // 设置最大个数。
        final int maxKeys = 200;
        // 列举文件。
        bucketName = fileType.equals("img") ? aliyunOSSConfig.getBucketNameImg() : aliyunOSSConfig.getBucketNameChapter();
        ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).withMaxKeys(maxKeys));
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        return sums;
    }

    /**
     * 根据文件名删除文件
     *
     * @param objectName
     * @return
     */
    public FileUploadResult delete(String objectName, String fileType) {
        bucketName = fileType.equals("img") ? aliyunOSSConfig.getBucketNameImg() : aliyunOSSConfig.getBucketNameChapter();
        // 根据BucketName,objectName删除文件
        ossClient.deleteObject(bucketName, objectName);
        FileUploadResult fileUploadResult = new FileUploadResult();
        fileUploadResult.setName(objectName);
        fileUploadResult.setStatus("removed");
        fileUploadResult.setResponse("success");
        return fileUploadResult;
    }

    /**
     * 下载文件
     *
     * @param os
     * @param objectName
     * @throws IOException
     */
    public void exportOssFile(OutputStream os, String objectName, String fileType) throws IOException {
        bucketName = fileType.equals("img") ? aliyunOSSConfig.getBucketNameImg() : aliyunOSSConfig.getBucketNameChapter();
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        // 读取文件内容。
        BufferedInputStream in = new BufferedInputStream(ossObject.getObjectContent());
        BufferedOutputStream out = new BufferedOutputStream(os);
        byte[] buffer = new byte[1024];
        int lenght = 0;
        while ((lenght = in.read(buffer)) != -1) {
            out.write(buffer, 0, lenght);
        }
        if (out != null) {
            out.flush();
            out.close();
        }
        if (in != null) {
            in.close();
        }
    }

    /**
     * 根据key获得文件在阿里云OSS的访问地址
     *
     * @param key
     * @param fileType
     * @return
     */
    public String getObjectUrl(String key, String fileType) {
        bucketName = fileType.equals("img") ? aliyunOSSConfig.getBucketNameImg() : aliyunOSSConfig.getBucketNameChapter();
        OSSClient ossClient = new OSSClient(aliyunOSSConfig.getEndpoint(), aliyunOSSConfig.getAccessKeyId(), aliyunOSSConfig.getAccessKeySecret());
        // 设置URL过期时间为5分钟
        Date expiration = new Date(System.currentTimeMillis() + 5 * 60 * 1000);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        ossClient.shutdown();
        return url.toString();
    }
}