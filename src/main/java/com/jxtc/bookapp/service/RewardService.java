package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Reward;
import com.jxtc.bookapp.entity.RewardRank;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wyl
 * 用户打赏接口
 */
public interface RewardService {

    /**
     * 用户打赏
     *
     * @param reward
     */
    @Transactional
    int insert(Reward reward,Integer couponId);

    /**
     * 获得打赏排行
     *
     * @param bookId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<RewardRank> getPageRewards(int bookId, int pageIndex, int pageSize);
}
