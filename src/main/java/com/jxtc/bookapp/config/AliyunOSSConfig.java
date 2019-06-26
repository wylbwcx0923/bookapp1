package com.jxtc.bookapp.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author wyl
 * 阿里云OSS配置
 */
@Configuration
@PropertySource(value = {"classpath:application-oss.properties"})
@ConfigurationProperties(prefix = "aliyun")
public class AliyunOSSConfig {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    //图片文件上传目录和访问前缀
    private String bucketNameImg;
    private String urlPrefixImg;
    //图书章节文件上传目录和访问前缀
    private String bucketNameChapter;
    private String urlPrefixChapter;

    @Bean
    public OSS oSSClient() {
        return new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketNameImg() {
        return bucketNameImg;
    }

    public void setBucketNameImg(String bucketNameImg) {
        this.bucketNameImg = bucketNameImg;
    }

    public String getUrlPrefixImg() {
        return urlPrefixImg;
    }

    public void setUrlPrefixImg(String urlPrefixImg) {
        this.urlPrefixImg = urlPrefixImg;
    }

    public String getBucketNameChapter() {
        return bucketNameChapter;
    }

    public void setBucketNameChapter(String bucketNameChapter) {
        this.bucketNameChapter = bucketNameChapter;
    }

    public String getUrlPrefixChapter() {
        return urlPrefixChapter;
    }

    public void setUrlPrefixChapter(String urlPrefixChapter) {
        this.urlPrefixChapter = urlPrefixChapter;
    }
}