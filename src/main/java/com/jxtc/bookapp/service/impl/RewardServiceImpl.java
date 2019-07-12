package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.RewardMapper;
import com.jxtc.bookapp.mapper.UserCoinMapper;
import com.jxtc.bookapp.mapper.UserInfoMapper;
import com.jxtc.bookapp.service.ConsumeService;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.service.RewardService;
import com.jxtc.bookapp.utils.PageResult;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardMapper rewardMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ConsumeService consumeService;
    @Autowired
    private UserCoinMapper userCoinMapper;

    @Override
    public int insert(Reward reward) {
        //判断用户的打赏类型
        int type = reward.getType();
        int cost = 0;//打赏花费
        int num = reward.getMun();//打赏数量
        if (num <= 0) {
            return 0;
        }
        switch (type) {
            case ApiConstant.RewardType.BIXIN://比心
                cost = num * ApiConstant.RewardAmount.BIXIN;
                break;
            case ApiConstant.RewardType.ROSE://玫瑰
                cost = num * ApiConstant.RewardAmount.ROSE;
                break;
            case ApiConstant.RewardType.SIX://666
                cost = num * ApiConstant.RewardAmount.SIX;
                break;
            case ApiConstant.RewardType.CARS://豪车
                cost = num * ApiConstant.RewardAmount.CARS;
                break;
        }
        //判断当前用户的余额是否能够本次打赏消费
        UserCoinExample example = new UserCoinExample();
        example.createCriteria().andUserIdEqualTo(reward.getUserId());
        List<UserCoin> userCoins = userCoinMapper.selectByExample(example);
        if (userCoins != null && userCoins.size() > 0) {
            UserCoin userCoin = userCoins.get(0);
            Integer coin = userCoin.getCoin();
            if (coin < cost) {
                return 0;
            } else {
                reward.setAmount(cost);
                reward.setCreateTime(new Date());
                //书币充足,进行打赏
                userCoinMapper.addCoinByUserId(reward.getUserId(), -cost);
                //打赏成功,记录消费
                try {
                    consumeService.addConsume(reward.getUserId(), reward.getBookId(), 0, reward.getAmount());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //清除打赏列表的缓存
                redisService.remove(reward.getBookId() + "reward");
                return rewardMapper.insert(reward);
            }
        }
        return 0;
    }

    @Override
    public PageResult<RewardRank> getPageRewards(int bookId, int pageIndex, int pageSize) {
        PageResult<RewardRank> page = new PageResult<>();
        List<RewardRank> list = new ArrayList<>();
        //先从redis中取
        String isExists = (String) redisService.hmGet(bookId + "reward", pageIndex + "_" + pageSize);
        if (isExists == null || "".equals(isExists) || isExists.length() < 5) {
            list = rewardMapper.selectTotals((pageIndex - 1) * pageSize, pageSize, bookId);
            String listStr = JSONArray.fromObject(list).toString();
            redisService.hmSet(bookId + "reward", pageIndex + "_" + pageSize, listStr);
        } else {
            JSONArray array = JSONArray.fromObject(isExists);
            list = (List<RewardRank>) JSONArray.toCollection(array, RewardRank.class);
        }
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        int total = rewardMapper.countTotals(bookId);
        page.setTotal(total);
        page.setPageList(list);
        return page;
    }
}
