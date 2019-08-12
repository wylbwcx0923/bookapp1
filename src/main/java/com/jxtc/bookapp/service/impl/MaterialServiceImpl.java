package com.jxtc.bookapp.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.jxtc.bookapp.entity.Material;
import com.jxtc.bookapp.entity.MaterialExample;
import com.jxtc.bookapp.mapper.app.MaterialMapper;
import com.jxtc.bookapp.service.MaterialService;
import com.jxtc.bookapp.utils.PageResult;
import com.jxtc.bookapp.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MaterialMapper materialMapper;

    @Override
    public String materialUpload(MultipartFile uploadFile) {
        if (uploadFile == null) {
            return "文件为空请重新选择";
        }
        InputStream inputStream = null;
        try {
            inputStream = uploadFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
        final String ACCESSKEYID = "KtH6vdN2XMCqoYDi";
        final String ACCESSKEYSECRET = "3ZPnJ6JVvdUdO1rv4X5GzKiIxmV9Rq";
        final String BUCKETNAME = "jxxs-app-img";
        String url = null;
        String key = "banner/" + TimeUtil.getTime().get("year") + "/" + TimeUtil.getTime().get("month") + "/" + TimeUtil.getTime().get("day") + "/" + System.currentTimeMillis() + ".jpg";
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
        logger.info("图片上传成功");
        return url;
    }

    @Override
    public void insertMaterialToMysql(String url, String materialName) {
        //将上传到oss的素材的路径保存到数据库
        Material material = new Material();
        material.setName(materialName);
        material.setUrl(url);
        material.setCreateTime(new Date());
        materialMapper.insertSelective(material);
    }

    @Override
    public PageResult<Material> getMaterialByPage(Integer pageIndex, Integer pageSize) {
        PageResult<Material> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        MaterialExample example = new MaterialExample();
        example.createCriteria();
        int total = materialMapper.countByExample(example);
        page.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<Material> materials = materialMapper.selectListByPage(null, offset, pageSize);
        page.setPageList(materials);
        return page;
    }

    @Override
    public void delMaterialById(int[] ids) {
        if (ids != null && ids.length > 0) {
            for (int id : ids) {
                materialMapper.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public PageResult<Material> searchMaterials(String name, int pageIndex, int pageSize) {
        PageResult<Material> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        int total = materialMapper.countByName(name);
        page.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<Material> materials = materialMapper.selectListByPage(name, offset, pageSize);
        page.setPageList(materials);
        return page;
    }
}
