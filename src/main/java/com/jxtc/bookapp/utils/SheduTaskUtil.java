package com.jxtc.bookapp.utils;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.entity.UserInfo;
import com.jxtc.bookapp.entity.UserInfoExample;
import com.jxtc.bookapp.entity.UserVip;
import com.jxtc.bookapp.entity.UserVipExample;
import com.jxtc.bookapp.mapper.UserInfoMapper;
import com.jxtc.bookapp.mapper.UserVipMapper;
import com.jxtc.bookapp.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author wyl
 * spring boot定时任务配置
 */
@Service
public class SheduTaskUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserVipMapper userVipMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 该方法为定时执行任务,用于自动过期掉到期的vip用户
     */
    @Scheduled(cron = "0 */1 * * * ?")//每分钟执行一次
    @Transactional//该方法配置事务支持
    public void vipExpire() {
        //查询出今天会过期的用户
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date tomorrow = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
        UserVipExample example = new UserVipExample();
        example.createCriteria().andExpireTimeBetween(c.getTime(), tomorrow).andStatusEqualTo(ApiConstant.VipStatus.DREDGE);
        List<UserVip> userVips = userVipMapper.selectByExample(example);
        if (userVips != null && userVips.size() > 0) {
            for (UserVip vip : userVips) {
                if (vip.getExpireTime().getTime() <= new Date().getTime()) {
                    vip.setStatus(ApiConstant.VipStatus.EXPIRE);//过期
                    logger.info("执行了过期");
                    userVipMapper.updateByPrimaryKey(vip);
                    //将用户表中Vip用户的类型改为普通用户
                    UserInfo userInfo = new UserInfo();
                    userInfo.setType(ApiConstant.UserType.GENNERAL_USER);
                    UserInfoExample exampleUser = new UserInfoExample();
                    exampleUser.createCriteria().andUserIdEqualTo(vip.getUserId());
                    userInfoService.clearUserByRedis(vip.getUserId());
                    userInfoMapper.updateByExampleSelective(userInfo, exampleUser);
                }
            }
        }
    }
}
