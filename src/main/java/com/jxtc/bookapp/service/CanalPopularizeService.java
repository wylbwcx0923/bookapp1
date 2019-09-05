package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Canal;
import com.jxtc.bookapp.entity.CanalPopularizeCount;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 渠道推广服务接口
 */
public interface CanalPopularizeService {
    /**
     * 添加渠道
     *
     * @param canal
     */
    @Transactional
    void addCanal(Canal canal);

    /**
     * 修改渠道
     *
     * @param canal
     */
    void updateCanal(Canal canal);

    /**
     * 获得渠道列表
     *
     * @return
     */
    List<Canal> getCanalList();

    /**
     * 删除渠道对象
     *
     * @param id
     */
    void deleteCanal(int id);

    /**
     * 根据不同的条件去查询渠道推广的统计列表
     *
     * @param pageIndex
     * @param pageSize
     * @param canalId
     * @param startTime
     * @param endTime
     * @return
     */
    PageResult<CanalPopularizeCount> getCanalPopularizeCountList(int pageIndex, int pageSize, Integer canalId, String startTime, String endTime);

    /**
     * 设置备注或成本
     *
     * @param remark
     * @param id
     * @param cose
     */
    void setCanalPopularizeCountRemarkOrCose(String remark, int id, Double cose);

    /**
     * 获得每个渠道的总人数
     *
     * @return
     */
    Map<String, Object> getDifferenceCanalUserCounts();
}
