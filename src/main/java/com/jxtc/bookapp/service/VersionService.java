package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Version;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 不忘初心
 * app版本更新服务
 */
public interface VersionService {
    //获取当前的最新版本
    Version getBestNewVersion();
    //上传最新的版本到服务器
    boolean updateAppToOss(MultipartFile file, Version version);
}
