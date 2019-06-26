package com.jxtc.bookapp.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.BannerImgsMapper;
import com.jxtc.bookapp.mapper.BannerMapper;
import com.jxtc.bookapp.mapper.MaterialMapper;
import com.jxtc.bookapp.service.BannerService;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.utils.PageResult;
import com.jxtc.bookapp.utils.TimeUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private MaterialMapper materialMapper;
    @Autowired
    private BannerMapper bannerMapper;
    @Autowired
    private BannerImgsMapper bannerImgsMapper;
    @Autowired
    private RedisService redisService;


    @Override
    public void materialUpload(MultipartFile uploadFile, String materialName) {
        if (uploadFile == null) {
            return;
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
        //将上传到oss的素材的路径保存到数据库
        Material material = new Material();
        material.setName(materialName);
        material.setUrl(url);
        material.setCreateTime(new Date());
        materialMapper.insertSelective(material);
        System.out.println("图片上传成功");
    }

    @Override
    public void insertBanner(Banner banner) {
        bannerMapper.insertSelective(banner);
    }

    @Override
    public PageResult<Banner> getBannerList(int pageIndex, int pageSize) {
        PageResult<Banner> page = new PageResult<>();
        BannerExample example = new BannerExample();
        int total = bannerMapper.countByExample(example);
        page.setTotal(total);
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        List<Banner> bannerList = bannerMapper.selectBannerListByPage((pageIndex - 1) * pageSize, pageSize);
        page.setPageList(bannerList);
        return page;
    }

    @Override
    public void deleteBanner(int id) {
        Banner banner = bannerMapper.selectByPrimaryKey(id);
        if (banner == null) {
            return;
        }
        int bannerId = banner.getBannerId();
        //删除该推荐banner位中的所有图片
        BannerImgsExample example = new BannerImgsExample();
        example.createCriteria().andBannerIdEqualTo(bannerId);
        redisService.remove(bannerId + "banner");
        bannerImgsMapper.deleteByExample(example);
        bannerMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Banner findBannerById(int id) {
        return bannerMapper.selectByPrimaryKey(id);
    }

    @Override
    public void insertBannerImgToBanner(BannerImgs bannerImgs) {
        bannerImgs.setCreateTime(new Date());
        redisService.remove(bannerImgs.getBannerId() + "banner");
        bannerImgsMapper.insertSelective(bannerImgs);
    }

    @Override
    public void deleteBannerImgToBanner(int id) {
        BannerImgs bannerImgs = bannerImgsMapper.selectByPrimaryKey(id);
        redisService.remove(bannerImgs.getBannerId() + "banner");
        bannerImgsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<BannerImgs> getBannerImageByBannerId(int bannerId) {
        List<BannerImgs> bannerImgs = new ArrayList<>();
        String isExists = (String) redisService.get(bannerId + "banner");
        if (isExists == null || isExists.length() < 5 || "".equals(isExists)) {
            //缓存中不存在,从MySQL中取
            bannerImgs = bannerImgsMapper.selectBannerImgs(bannerId);
            if (bannerImgs != null && bannerImgs.size() > 0) {
                //放入缓存
                String bannerImgsStr = JSONArray.fromObject(bannerImgs).toString();
                redisService.set(bannerId + "banner", bannerImgsStr);
            }
        }else{
            //缓存中存在 从缓存中取
            JSONArray array=JSONArray.fromObject(isExists);
            bannerImgs= (List<BannerImgs>) JSONArray.toCollection(array,BannerImgs.class);
        }
        return bannerImgs;
    }


}
