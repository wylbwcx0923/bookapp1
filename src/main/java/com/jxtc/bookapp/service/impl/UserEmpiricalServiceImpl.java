package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.entity.UserEmpirical;
import com.jxtc.bookapp.entity.UserEmpiricalExample;
import com.jxtc.bookapp.mapper.UserCoinMapper;
import com.jxtc.bookapp.mapper.UserEmpiricalMapper;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.service.UserCouponService;
import com.jxtc.bookapp.service.UserEmpiricalService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserEmpiricalServiceImpl implements UserEmpiricalService {

    @Autowired
    private UserEmpiricalMapper userEmpiricalMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserCoinMapper userCoinMapper;
    @Autowired
    private UserCouponService userCouponService;

    @Override
    public void upgrade(String userId, int empirical) {
        UserEmpirical userEmpirical = findEmpiricalByUserId(userId);
        //得到原来的用户的经验值
        int beforeEmpirical = userEmpirical.getEmpirical();
        //得到当前的用户的经验值
        int nowEmpirical = userEmpirical.getEmpirical() + empirical;
        //更新用户的经验值
        UserEmpiricalExample empiricalExample = new UserEmpiricalExample();
        empiricalExample.createCriteria().andUserIdEqualTo(userId);
        UserEmpirical empiricalUp = new UserEmpirical();
        empiricalUp.setEmpirical(nowEmpirical);
        //0-2000:普通用户;2001-3000:铜牌;3000-4000:银牌;4000-5000金牌;5000以上钻石
        if (beforeEmpirical < ApiConstant.EmpiricalCrisis.COPPER && nowEmpirical >= ApiConstant.EmpiricalCrisis.COPPER && nowEmpirical < ApiConstant.EmpiricalCrisis.AGUSER) {
            //用户从普通用户升级到了铜牌
            empiricalUp.setGrade(ApiConstant.UserGrade.COPPER);
        }
        if (beforeEmpirical < ApiConstant.EmpiricalCrisis.AGUSER && nowEmpirical >= ApiConstant.EmpiricalCrisis.AGUSER && nowEmpirical < ApiConstant.EmpiricalCrisis.GOLD) {
            //用户升到了银牌
            empiricalUp.setGrade(ApiConstant.UserGrade.AGUSER);
            empiricalUp.setUserDiscount(ApiConstant.UserDiscount.AGUSER);
            //送1张8.5折优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.DISCOUNT, 0.85, 0, 0, "0", 0, "升级大礼包", 7);
            //送1张5元满减优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.VOUCHER, 0, 5, 0, "0", 40, "升级大礼包", 3);
            //送1张5折优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.DISCOUNT, 0.5, 0, 0, "1", 0, "升级大礼包", 1);
            //送2张比心打赏优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.REWARD, 0, 0, ApiConstant.RewardType.BIXIN, "0", 0, "升级大礼包", 15);
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.REWARD, 0, 0, ApiConstant.RewardType.BIXIN, "0", 0, "升级大礼包", 15);
        }
        if (beforeEmpirical < ApiConstant.EmpiricalCrisis.GOLD && nowEmpirical >= ApiConstant.EmpiricalCrisis.GOLD && nowEmpirical < ApiConstant.EmpiricalCrisis.DIAMOND) {
            //用户升到了金牌
            empiricalUp.setGrade(ApiConstant.UserGrade.GOLD);
            empiricalUp.setUserDiscount(ApiConstant.UserDiscount.GOLD);
            //送2张7.5折优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.DISCOUNT, 0.75, 0, 0, "0", 0, "升级大礼包", 7);
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.DISCOUNT, 0.75, 0, 0, "0", 0, "升级大礼包", 7);
            //送1张8元满减优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.VOUCHER, 0, 8, 0, "0", 40, "升级大礼包", 3);
            //送1张5折优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.DISCOUNT, 0.5, 0, 0, "1", 0, "升级大礼包", 1);
            //送2张比心打赏优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.REWARD, 0, 0, ApiConstant.RewardType.ROSE, "0", 0, "升级大礼包", 15);
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.REWARD, 0, 0, ApiConstant.RewardType.ROSE, "0", 0, "升级大礼包", 15);
        }
        if (beforeEmpirical < ApiConstant.EmpiricalCrisis.DIAMOND && nowEmpirical >= ApiConstant.EmpiricalCrisis.DIAMOND) {
            //用户升到了钻石
            empiricalUp.setGrade(ApiConstant.UserGrade.DIAMOND);
            empiricalUp.setUserDiscount(ApiConstant.UserDiscount.DIAMOND);
            //送3张7折优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.DISCOUNT, 0.7, 0, 0, "0", 0, "升级大礼包", 7);
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.DISCOUNT, 0.7, 0, 0, "0", 0, "升级大礼包", 7);
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.DISCOUNT, 0.7, 0, 0, "0", 0, "升级大礼包", 7);
            //送1张10元满减优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.VOUCHER, 0, 10, 0, "0", 40, "升级大礼包", 3);
            //送1张5折优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.DISCOUNT, 0.5, 0, 0, "1", 0, "升级大礼包", 1);
            //送2张比心打赏优惠券
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.REWARD, 0, 0, ApiConstant.RewardType.CARS, "0", 0, "升级大礼包", 15);
            userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.REWARD, 0, 0, ApiConstant.RewardType.CARS, "0", 0, "升级大礼包", 15);
        }
        //修改用户的经验值和等级和折扣
        userEmpiricalMapper.updateByExampleSelective(empiricalUp, empiricalExample);
    }


    @Override
    public UserEmpirical findEmpiricalByUserId(String userId) {
        //从缓存中取用户的经验值
        String isExists = (String) redisService.get("EMPIRICAL_" + userId);
        UserEmpirical userEmpirical = new UserEmpirical();
        if (StringUtils.isEmpty(isExists)) {
            //如果取不到,从MYSQL中取
            UserEmpiricalExample example = new UserEmpiricalExample();
            example.createCriteria().andUserIdEqualTo(userId);
            List<UserEmpirical> userEmpiricals = userEmpiricalMapper.selectByExample(example);
            if (userEmpiricals != null && userEmpiricals.size() > 0) {
                userEmpirical = userEmpiricals.get(0);
                //把取到的经验值对象放入缓存
                String objectStr = JSONObject.fromObject(userEmpirical).toString();
                redisService.set("EMPIRICAL_" + userId, objectStr);
            }
        } else {
            //如果缓存中存在,直接从缓存中取
            JSONObject object = JSONObject.fromObject(isExists);
            userEmpirical = (UserEmpirical) JSONObject.toBean(object, UserEmpirical.class);
        }

        return userEmpirical;
    }

    @Override
    public void doTask(String userId, int taskType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        switch (taskType) {
            case ApiConstant.TaskType.SIGN_IN:
                //签到,送书币
                userCoinMapper.addCoinByUserId(userId, 10);
                //记录今天已签到
                redisService.set("SIGN_IN_" + sdf.format(new Date()) + "_" + userId, "true", ApiConstant.Timer.ONE_DAY);
                break;
            case ApiConstant.TaskType.READ:
                //阅读书籍
                redisService.set("READ_" + sdf.format(new Date()) + "_" + userId, "true", ApiConstant.Timer.ONE_DAY);
                break;
            case ApiConstant.TaskType.SHARE:
                //分享,送优惠券
                userCouponService.sendCouponToUser(userId, ApiConstant.CouponType.VOUCHER, 0, 5, 0, "0", 0, "分享礼包", 1);
                redisService.set("SHARE_" + sdf.format(new Date()) + "_" + userId, "true", ApiConstant.Timer.ONE_DAY);
                break;
            case ApiConstant.TaskType.ASSIGN_READ:
                //指定阅读书籍
                redisService.set("ASSIGN_READ_" + sdf.format(new Date()) + "_" + userId, "true", ApiConstant.Timer.ONE_DAY);
                break;
            case ApiConstant.TaskType.BIND_WX:
                //绑定微信
                redisService.set("BIND_WX_" + userId, "true");
                break;
            case ApiConstant.TaskType.BIND_QQ:
                //绑定QQ
                redisService.set("BIND_QQ_" + userId, "true");
                break;
        }
        //统一送经验值
        upgrade(userId, 10);
    }

    @Override
    public Map<String, Boolean> checkTaskStatus(String userId) {
        Map<String, Boolean> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String[] tasks = {"SIGN_IN_", "READ_", "SHARE_", "ASSIGN_READ_", "BIND_WX_", "BIND_QQ_"};
        for (String task : tasks) {
            String flag = null;
            if ("BIND_WX_".equals(task) || "BIND_QQ_".equals(task)) {
                flag = (String) redisService.get("BIND_WX_" + userId);
            } else {
                flag = (String) redisService.get(task + sdf.format(new Date()) + "_" + userId);
            }
            if (StringUtils.isNotEmpty(flag) && "true".equals(flag)) {
                //说明今天已经完成了该任务
                map.put(task, true);
            } else {
                //今天没完成的任务
                map.put(task, false);
            }
        }
        return map;
    }
}
