package com.jxtc.bookapp.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.jxtc.bookapp.entity.Version;
import com.jxtc.bookapp.mapper.app.VersionMapper;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.service.VersionService;
import com.jxtc.bookapp.utils.TimeUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service(value = "versionService")
public class VersionServiceImpl implements VersionService {
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public Version getBestNewVersion() {
        Version version = new Version();
        //从缓存中取最新的系统版本
        String redisVersion = (String) redisService.get("new_jxapp_version");
        if (StringUtils.isEmpty(redisVersion)) {
            //缓存取不到的话去MySQL中取
            version = versionMapper.selectBaseNewVersion();
            String[] versionAndSize = version.getVersion().split(",");
            version.setAppSize(versionAndSize[0]);
            version.setVersion(versionAndSize[1]);
            //将取得的版本对象放入redis中
            String versionStr = JSONObject.fromObject(version).toString();
            redisService.set("new_jxapp_version", versionStr);
        } else {
            //直接从redis中取
            JSONObject object = JSONObject.fromObject(redisVersion);
            version = (Version) JSONObject.toBean(object, Version.class);
        }
        return version;
    }

    @Override
    public boolean updateAppToOss(MultipartFile file, Version version) {
        Map<String, Object> uploadRes = apkUpload(file);
        int rows = 0;
        if (uploadRes != null && uploadRes.size() > 0) {
            boolean flag = (boolean) uploadRes.get("flag");
            if (flag) {
                //程序执行至此,说明文件上传成功
                String apkUrl = (String) uploadRes.get("url");
                System.out.println(apkUrl);
                version.setDownloadUrl(apkUrl);
                version.setUpdateTime(new Date());
                version.setVersion(uploadRes.get("appsize") + "," + version.getVersion());
                rows = versionMapper.insertSelective(version);
            }
        }
        if (rows > 0) {
            redisService.remove("new_jxapp_version");
            return true;
        }
        return false;
    }

    /**
     * 上传apk文件的方法
     *
     * @param file
     * @return
     */
    private Map<String, Object> apkUpload(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file == null) {
            result.put("flag", false);
            result.put("msg", "文件为空请重新选择");
            return result;
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            double available = inputStream.available();//获取的是文件的字节大小
            System.out.println("上传文件的大小为:" + available);
            //将文件大小转化为兆B级别
            double sizeDouble = available / 1024 / 1024;
            //定义数字格式化
            String size = String.format("%.2f",sizeDouble) + "MB";
            result.put("appsize", size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
        final String ACCESSKEYID = "KtH6vdN2XMCqoYDi";
        final String ACCESSKEYSECRET = "3ZPnJ6JVvdUdO1rv4X5GzKiIxmV9Rq";
        final String BUCKETNAME = "jxxs-app-apk";
        String url = null;
        String key = "apk/" + TimeUtil.getTime().get("year") + "/" + TimeUtil.getTime().get("month") + "/" + TimeUtil.getTime().get("day") + "/" + System.currentTimeMillis() + ".apk";
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
        try {
            // 带进度条的上传
            ossClient.putObject(new PutObjectRequest(BUCKETNAME, key, inputStream));
        } catch (OSSException oe) {
            oe.printStackTrace();
            key = null;
        } catch (ClientException ce) {
            ce.printStackTrace();
            key = null;
        } catch (Exception e) {
            e.printStackTrace();
            key = null;
        } finally {
            ossClient.shutdown();
        }
        if (key != null) {
            // 拼接文件访问路径。由于拼接的字符串大多为String对象，而不是""的形式，所以直接用+拼接的方式没有优势
            StringBuffer sb = new StringBuffer();
            sb.append("http://").append(BUCKETNAME).append(".").append(ENDPOINT
            ).append("/").append(key);
            url = sb.toString();
        }
        result.put("flag", true);
        result.put("url", url);
        System.out.println(url);
        return result;
    }
}
