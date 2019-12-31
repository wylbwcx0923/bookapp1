package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.WxConfig;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.app.*;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.service.UserEmpiricalService;
import com.jxtc.bookapp.service.UserInfoService;
import com.jxtc.bookapp.utils.PageResult;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.user.User;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 用户服务
 */
@Service(value = "userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WxConfig wxConfig;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserEmpiricalMapper userEmpiricalMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserVipMapper userVipMapper;
    @Autowired
    private UserCoinMapper userCoinMapper;
    @Autowired
    private UserEmpiricalService userEmpiricalService;

    /**
     * 微信登录服务
     *
     * @param code
     * @param canalId
     * @return
     */
    @Override
    public String wxLogin(String code, Integer canalId) {
        String appid = wxConfig.getAppid();//获得app在微信开放平台的唯一标识
        String secret = wxConfig.getSecret();//获得该appid对应的secret
        //获得access_token
        SnsToken token = SnsAPI.oauth2AccessToken(appid, secret, code);
        //根据token完成认证
        BaseResult baseResult = SnsAPI.auth(token.getAccess_token(), token.getOpenid());
        //认证成功
        if ("0".equals(baseResult.getErrcode()) && "ok".equals(baseResult.getErrmsg())) {
            UserInfo userInfo = null;
            //判断该用户是否已经用微信登录过
            UserInfoExample example = new UserInfoExample();
            example.createCriteria().andOpenidEqualTo(token.getOpenid());
            List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
            if (userInfos == null || userInfos.size() <= 0) {
                //获得用户信息
                User user = SnsAPI.userinfo(token.getAccess_token(), token.getOpenid(), "zh-CN");
                userInfo = new UserInfo();
                String userId = getUserId();
                userInfo.setUserId(userId);
                userInfo.setCity(user.getCity());
                userInfo.setCountry(user.getNickname());
                userInfo.setCreateTime(new Date());
                userInfo.setNickname(user.getNickname());
                userInfo.setUpdateTime(new Date());
                userInfo.setOpenid(user.getOpenid());
                if (StringUtils.isEmpty(user.getHeadimgurl())) {
                    userInfo.setHeadimgurl(ApiConstant.Config.DEFULT_HEAD);
                } else {
                    userInfo.setHeadimgurl(user.getHeadimgurl());
                }
                //设置渠道Id
                canalId = canalId == null ? 0 : canalId;
                userInfo.setCoin(canalId);
                userInfo.setProvince(user.getProvince());
                userInfo.setSex(user.getSex() == null ? 1 : user.getSex());
                userInfo.setType(ApiConstant.UserType.GENNERAL_USER);
                userInfoMapper.insert(userInfo);
                //在用户经验值的表中插入一条该用户的数据
                createUserEmpirical(userId);
                //在用户的阅币表中插入一条数据,初始化用户的阅币账户
                initUserCoin(userId);
                return userId;
            }
            return userInfos.get(0).getUserId();
        }
        return null;
    }


    /**
     * 初始化用户的阅币
     *
     * @param userId
     */
    private void initUserCoin(String userId) {
        UserCoin userCoin = new UserCoin();
        userCoin.setCoin(0);
        userCoin.setUserId(userId);
        userCoinMapper.insertSelective(userCoin);
    }

    @Override
    public String qqLogin(UserInfo userInfo) {
        if (userInfo != null) {
            String qq = userInfo.getQq();
            UserInfoExample example = new UserInfoExample();
            example.createCriteria().andQqEqualTo(qq);
            List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
            if (userInfos == null || userInfos.size() <= 0) {
                String userId = getUserId();
                userInfo.setPassword(userInfo.getNickname());
                userInfo.setUserId(userId);
                userInfo.setType(ApiConstant.UserType.GENNERAL_USER);
                int canalId = userInfo.getCoin() == null ? 0 : userInfo.getCoin();
                userInfo.setCoin(canalId);
                userInfo.setCreateTime(new Date());
                userInfo.setUpdateTime(new Date());
                userInfoMapper.insert(userInfo);
                createUserEmpirical(userInfo.getUserId());
                //在用户的阅币表中插入一条数据,初始化用户的阅币账户
                initUserCoin(userId);
                return userId;
            }
            return userInfos.get(0).getUserId();
        }
        return null;
    }

    @Override
    public UserInfo getUserInfoByLocal(String userId) {
        //从缓存中取用户信息
        UserInfo userInfo = new UserInfo();
        String isExists = (String) redisService.hmGet("userInfo", userId);
        if (StringUtils.isEmpty(isExists)) {
            //取不到从mysql取
            UserInfoExample example = new UserInfoExample();
            example.createCriteria().andUserIdEqualTo(userId);
            List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
            if (userInfos != null && userInfos.size() > 0) {
                //获得之后放入缓存
                userInfo = userInfos.get(0);
                String objectStr = JSONObject.fromObject(userInfo).toString();
                redisService.hmSet("userInfo", userId, objectStr);
            }
        } else {
            JSONObject object = JSONObject.fromObject(isExists);
            userInfo = (UserInfo) JSONObject.toBean(object, UserInfo.class);
        }
        return userInfo;
    }

    @Override
    public void clearUserByRedis(String userId) {
        //清除缓存
        redisService.hmSet("userInfo", userId, "");
    }

    @Override
    public Map<String, Object> getUserInfo(String userId) {
        Map<String, Object> map = new HashMap<>();
        //首先从缓存中取看能不能取到
        String isExists = (String) redisService.hmGet("userInfo", userId);
        if (StringUtils.isNotEmpty(isExists)) {
            clearUserByRedis(userId);
        }
        UserInfo userInfo = getUserInfoByLocal(userId);
        //获得用户的阅币
        UserCoinExample userCoinexample = new UserCoinExample();
        userCoinexample.createCriteria().andUserIdEqualTo(userId);
        List<UserCoin> userCoins = userCoinMapper.selectByExample(userCoinexample);
        if (userCoins != null && userCoins.size() > 0) {
            UserCoin coin = userCoins.get(0);
            userInfo.setCoin(coin.getCoin());
        }
        //获得用户的经验值
        UserEmpirical empirical = userEmpiricalService.findEmpiricalByUserId(userId);
        if (empirical != null) {
            map.put("empircal", empirical.getEmpirical());
            map.put("grade", empirical.getGrade());
        }
        //获得用户的vip过期时间
        if (userInfo.getType() != ApiConstant.UserType.GENNERAL_USER) {
            UserVipExample exampleVip = new UserVipExample();
            exampleVip.createCriteria().andUserIdEqualTo(userId);
            List<UserVip> userVips = userVipMapper.selectByExample(exampleVip);
            if (userVips != null && userVips.size() > 0) {
                UserVip userVip = userVips.get(0);
                map.put("vipType", userVip.getVipType());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                map.put("expireTime", sdf.format(userVip.getExpireTime()));
            }
        }
        map.put("userInfo", userInfo);
        return map;
    }

    @Override
    public void updateUser(UserInfo userInfo) {
        UserInfoExample example = new UserInfoExample();
        example.createCriteria().andUserIdEqualTo(userInfo.getUserId());
        userInfo.setUpdateTime(new Date());
        userInfoMapper.updateByExampleSelective(userInfo, example);
    }

    @Override
    public void bindWeiXin(String userId, String code) {
        UserInfoExample example = new UserInfoExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
        if (userInfos != null && userInfos.size() > 0) {
            UserInfo userInfo = userInfos.get(0);
            String appid = wxConfig.getAppid();//获得app在微信开放平台的唯一标识
            String secret = wxConfig.getSecret();//获得该appid对应的secret
            //获得access_token
            SnsToken token = SnsAPI.oauth2AccessToken(appid, secret, code);
            //根据token完成认证
            BaseResult baseResult = SnsAPI.auth(token.getAccess_token(), token.getOpenid());
            //认证成功
            if ("0".equals(baseResult.getErrcode()) && "ok".equals(baseResult.getErrmsg())) {
                //获得用户信息
                User user = SnsAPI.userinfo(token.getAccess_token(), token.getOpenid(), "zh-CN");
                userInfo.setOpenid(user.getOpenid());
                userInfo.setHeadimgurl(user.getHeadimgurl());
                userInfo.setSex(user.getSex());
                userInfo.setCountry(user.getNickname());
                userInfo.setUpdateTime(new Date());
                //更新
                userInfoMapper.updateByExampleSelective(userInfo, example);
            }
        }

    }

    @Override
    public void bindQQ(String userId, UserInfo userQQ) {
        UserInfoExample example = new UserInfoExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
        if (userInfos != null && userInfos.size() > 0) {
            UserInfo userInfo = userInfos.get(0);
            userInfo.setHeadimgurl(userQQ.getHeadimgurl());
            userInfo.setQq(userQQ.getQq());
            userInfo.setPassword(userQQ.getNickname());
            //更新
            userInfoMapper.updateByExampleSelective(userInfo, example);
        }
    }

    /**
     * 创建用户的经验值
     *
     * @param userId
     */
    private void createUserEmpirical(String userId) {
        UserEmpirical empirical = new UserEmpirical();
        empirical.setEmpirical(0);
        empirical.setGrade(ApiConstant.UserGrade.GENERAL);
        empirical.setCreateTime(new Date());
        empirical.setUpdateTime(new Date());
        empirical.setUserDiscount(ApiConstant.UserDiscount.GENERAL);
        empirical.setUserId(userId);
        userEmpiricalMapper.insert(empirical);
    }

    /**
     * 自动生成用户id
     *
     * @param currMaxId
     * @return
     */
    private String getUserId() {
        int currMaxId = userInfoMapper.getMaxId();//获得当前最大的id
        StringBuffer userId = new StringBuffer("jx");
        int originalId = 100000;
        int newId = originalId + currMaxId + 1;
        userId.append(newId);
        return userId.toString();
    }

    @Override
    public Map<String, Object> smsVerify(String phoneNumber, String code, Integer canalId) {
        Map<String, Object> result = new HashMap<>();
        //判断用户的验证码是否过期
        String flagCode = (String) redisService.get("SMS_" + phoneNumber);
        if (StringUtils.isEmpty(flagCode)) {
            //验证码已经过期
            result.put("errorCode", 201);
            result.put("msg", "验证码过期,请重新获取!");
            return result;
        }
        //程序执行至此,说明验证码肯定不过期
        //判断用户输入的验证码和生成的验证码是否匹配
        if (!flagCode.equals(code)) {
            //输入和生成不匹配
            result.put("errorCode", 202);
            result.put("msg", "验证码输入错误!");
            return result;
        }
        //程序执行至此,说明验证通过
        //判断用户是否以前用该手机号注册过
        UserInfoExample example = new UserInfoExample();
        example.createCriteria().andPhoneEqualTo(phoneNumber);
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
        if (userInfos != null && userInfos.size() > 0) {
            //以前注册过
            result.put("errorCode", 200);
            result.put("userId", userInfos.get(0).getUserId());
            return result;
        }
        //程序执行至此,说明第一次注册
        //构建默认的用户对象
        UserInfo userInfo = new UserInfo();
        String userId = getUserId();
        userInfo.setUserId(userId);
        userInfo.setPhone(phoneNumber);
        userInfo.setCreateTime(new Date());
        userInfo.setNickname("用户" + userId);
        userInfo.setUpdateTime(new Date());
        userInfo.setHeadimgurl(ApiConstant.Config.DEFULT_HEAD);
        //设置渠道Id
        canalId = canalId == null ? 0 : canalId;
        userInfo.setCoin(canalId);
        userInfo.setSex(2);//默认设置为女生
        userInfo.setType(ApiConstant.UserType.GENNERAL_USER);
        userInfoMapper.insert(userInfo);
        //在用户经验值的表中插入一条该用户的数据
        createUserEmpirical(userId);
        //在用户的阅币表中插入一条数据,初始化用户的阅币账户
        initUserCoin(userId);
        result.put("errorCode", 200);
        result.put("userId", userId);
        return result;
    }

    @Override
    public PageResult<UserInfo> getUserList(int pageIndex, int pageSize, String userId, String startTime, String endTime) {
        PageResult<UserInfo> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        int offset = (pageIndex - 1) * pageSize;
        List<UserInfo> userInfos = userInfoMapper.selectUserListByPage(offset, pageSize, userId, startTime, endTime);
        if (userInfos != null && userInfos.size() > 0) {
            for (UserInfo userInfo : userInfos) {
                Integer coin = userCoinMapper.getCoinByUserId(userInfo.getUserId());
                coin = coin == null ? 0 : coin;
                userInfo.setCoin(coin);
            }
        }
        page.setPageList(userInfos);
        int total = userInfoMapper.countUserList(userId, startTime, endTime);
        page.setTotal(total);
        return page;
    }

    @Override
    public void sendCoin(String userId, int coin) {
        userCoinMapper.addCoinByUserId(userId, coin);
    }

    @Override
    public void verbUser(String userId) {
        UserCoinExample example = new UserCoinExample();
        example.createCriteria().andUserIdEqualTo(userId);
        UserCoin userCoinUp = new UserCoin();
        userCoinUp.setCoin(0);
        userCoinMapper.updateByExampleSelective(userCoinUp, example);
    }

    @Override
    public Map<String, Object> userKeepCount() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //查询今日下载人数
        int todayDown = userInfoMapper.countUserList(null, sdf.format(new Date()), sdf.format(new Date()));
        int todayActi = userInfoMapper.countActiviteUser(sdf.format(new Date()), sdf.format(new Date()));
        //昨日下载人数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        int yesterdayDown = userInfoMapper.countUserList(null, sdf.format(calendar.getTime()), sdf.format(calendar.getTime()));
        int yesterdayActi = userInfoMapper.countActiviteUser(sdf.format(calendar.getTime()), sdf.format(calendar.getTime()));
        //本月人数
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-01");
        int monthDown = userInfoMapper.countUserList(null, sdf1.format(new Date()), sdf.format(new Date()));
        int monthActi = userInfoMapper.countActiviteUser(sdf1.format(new Date()), sdf.format(new Date()));
        int total = userInfoMapper.countUserList(null, null, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("todayDown", todayDown);
        map.put("yesterdayDown", yesterdayDown);
        map.put("monthDown", monthDown);
        map.put("total", total);
        map.put("todayActi", todayActi);
        map.put("yesterdayActi", yesterdayActi);
        map.put("monthActi", monthActi);
        return map;
    }

    @Override
    public PageResult<UserCount> getUserCounts(int pageIndex, int pageSize) {
        PageResult<UserCount> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        int total = userInfoMapper.countDays();
        page.setTotal(total);
        int offset = (pageIndex - 1) * pageSize;
        List<UserCount> userActiOneDays = userInfoMapper.selectUserCountActisList(offset, pageSize);
        if (userActiOneDays != null && userActiOneDays.size() > 0) {
            for (UserCount userActiOneDay : userActiOneDays) {
                userActiOneDay.setDownUser(userInfoMapper.countDownUser(userActiOneDay.getOneDay(), userActiOneDay.getOneDay()));
            }
        }
        page.setPageList(userActiOneDays);
        return page;
    }

    @Override
    public Map<String, Object> getUserKeepOneSevenAndMounth() {
        Map<String, Object> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //次日留存:注册时间在昨日,活动时间在今日的称为次日留存
        int nextDay = userInfoMapper.countUserKeepByTime(ApiConstant.KeepTime.NEXT_DAY, sdf.format(new Date()));
        //七日留存.注册时间和活动时间相差7日及以上称为7日留存
        int sevenDay = userInfoMapper.countUserKeepByTime(ApiConstant.KeepTime.SEVEN_DAY, sdf.format(new Date()));
        //三十日日留存.注册时间和活动时间相差7日及以上称为30日留存
        int monthDay = userInfoMapper.countUserKeepByTime(ApiConstant.KeepTime.MONTH_DAY, sdf.format(new Date()));
        //留存率
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        //次日留存率
        int nextDayRegis = userInfoMapper.countUserList(null, null, null);
        String nextDayRate = numberFormat.format((double) nextDay / (double) nextDayRegis * 3 * 100) + "%";
        //七日留存率
        int sevenDayRegis = userInfoMapper.countUserRegistByTime(ApiConstant.KeepTime.SEVEN_DAY, sdf.format(new Date()));
        String sevenDayRate = numberFormat.format((double) sevenDay / (double) sevenDayRegis / 10 * 100) + "%";
        //月留存率
        int monthDayRegis = userInfoMapper.countUserRegistByTime(ApiConstant.KeepTime.MONTH_DAY, sdf.format(new Date()));
        String monthDayRate = numberFormat.format((double) monthDay / (double) monthDayRegis * 100) + "%";
        map.put("nextDay", nextDay);
        map.put("nextDayRate", nextDayRate);
        map.put("sevenDay", sevenDay);
        map.put("sevenDayRate", sevenDayRate);
        map.put("monthDay", monthDay);
        map.put("monthDayRate", monthDayRate);
        return map;
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public String googleLogin(String googleId, String nickname, String photo) {
        //判断googleId是否是空
        if (StringUtils.isNotBlank(googleId)) {
            UserInfoExample example = new UserInfoExample();
            example.createCriteria().andPasswordEqualTo(googleId);
            List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
            //已经注册过
            if (userInfos != null && userInfos.size() > 0) {
                return userInfos.get(0).getUserId();
            }
            //程序执行至此,说明没有注册过
            UserInfo userInfo = new UserInfo();
            userInfo.setPassword(googleId);//传ID
            userInfo.setNickname(nickname);//传名字
            userInfo.setSex(2);
            if (StringUtils.isNotBlank(photo)) {
                userInfo.setHeadimgurl(photo);//传用户的头像
            } else {
                userInfo.setHeadimgurl(ApiConstant.Config.DEFULT_HEAD);
            }
            userInfo.setCoin(14);
            userInfo.setType(1);
            userInfo.setCreateTime(new Date());
            userInfo.setUpdateTime(new Date());
            String userId = getUserId();
            userInfo.setUserId(userId);
            userInfoMapper.insertSelective(userInfo);
            //在用户经验值的表中插入一条该用户的数据
            createUserEmpirical(userId);
            //在用户的阅币表中插入一条数据,初始化用户的阅币账户
            initUserCoin(userId);
            return userId;
        }
        return "google登录失败,请重试";
    }
}
