package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.WxConfig;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.app.UserCoinMapper;
import com.jxtc.bookapp.mapper.app.UserEmpiricalMapper;
import com.jxtc.bookapp.mapper.app.UserInfoMapper;
import com.jxtc.bookapp.mapper.app.UserVipMapper;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.service.UserEmpiricalService;
import com.jxtc.bookapp.service.UserInfoService;

import com.jxtc.bookapp.utils.PageResult;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import weixin.popular.api.SnsAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 用户服务
 */
@Service(value = "userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

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
     * @return
     */
    @Override
    public String wxLogin(String code) {
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
                userInfo.setProvince(user.getProvince());
                userInfo.setCoin(0);
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
                userInfo.setCoin(0);
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
    public Map<String, Object> smsVerify(String phoneNumber, String code) {
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
        userInfo.setCoin(0);
        userInfo.setSex(1);//默认设置为男生
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
    public PageResult<UserInfo> getUserList(int pageIndex, int pageSize, String userId) {
        PageResult<UserInfo> page = new PageResult<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        int offset = (pageIndex - 1) * pageSize;
        List<UserInfo> userInfos = userInfoMapper.selectUserListByPage(offset, pageSize, userId);
        if (userInfos != null && userInfos.size() > 0) {
            for (UserInfo userInfo : userInfos) {
                Integer coin = userCoinMapper.getCoinByUserId(userInfo.getUserId());
                coin = coin == null ? 0 : coin;
                userInfo.setCoin(coin);
            }
        }
        page.setPageList(userInfos);
        UserInfoExample example = new UserInfoExample();
        if (StringUtils.isEmpty(userId)) {
            example.createCriteria();
        } else {
            example.createCriteria().andUserIdEqualTo(userId);
        }
        int total = userInfoMapper.countByExample(example);
        page.setTotal(total);
        return page;
    }

    @Override
    public void sendCoin(String userId, int coin) {
        userCoinMapper.addCoinByUserId(userId, coin);
    }
}
