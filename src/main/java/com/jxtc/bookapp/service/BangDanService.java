package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Bangdan;
import com.jxtc.bookapp.entity.BangdanBooks;
import com.jxtc.bookapp.entity.Material;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 榜单服务
 */
public interface BangDanService {

    /**
     * 添加一个榜单
     *
     * @param bangdan
     * @return
     */
    int insertBangDan(Bangdan bangdan);

    /**
     * 分页显示榜单
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<Bangdan> getBangDanList(int pageIndex, int pageSize);

    /**
     * 根据id查询榜单详情
     *
     * @param id
     * @return
     */
    Bangdan findById(int id);

    /**
     * 修改榜单
     *
     * @param bangdan
     * @return
     */
    int updateBangDan(Bangdan bangdan);

    /**
     * 通过id删除榜单
     *
     * @param id
     * @return
     */
    @Transactional
    int deleteBangDan(int id);

    /**
     * 添加书到榜单
     *
     * @param bangdanBooks
     * @return
     */
    int insertBangDanBooks(BangdanBooks bangdanBooks);

    /**
     * 删除榜单中的书
     *
     * @param ids
     * @return
     */
    void deleteBangDanBooks(int ids);


    PageResult<BangdanBooks> getBangDanBooks(int bangDanId, int pageIndex, int pageSize);

    /**
     * 根据分类获得榜单的名字
     * @param type
     * @return
     */
    List<Bangdan> getBangDanListByType(int type);


}
