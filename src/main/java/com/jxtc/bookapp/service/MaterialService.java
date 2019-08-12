package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Material;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

public interface MaterialService {

    /**
     * 轮播图素材上传
     *
     * @param uploadFile
     */
    String materialUpload(MultipartFile uploadFile);

    /**
     * 保存素材到Mysql
     * @param url
     * @param materialName
     */
    void insertMaterialToMysql(String url,String materialName);

    /**
     * 分页获取素材列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<Material> getMaterialByPage(Integer pageIndex, Integer pageSize);

    /**
     * 根据id删除素材
     *
     * @param ids
     */
    void delMaterialById(int[] ids);

    /**
     * 根据名字搜索素材
     *
     * @param name
     * @return
     */
    PageResult<Material> searchMaterials(String name, int pageIndex, int pageSize);
}
