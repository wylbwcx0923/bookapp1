package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.WxConfig;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.UserEmpiricalMapper;
import com.jxtc.bookapp.mapper.UserInfoMapper;
import com.jxtc.bookapp.mapper.UserVipMapper;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.service.UserInfoService;

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
                userInfo.setCountry(user.getCountry());
                userInfo.setCreateTime(new Date());
                userInfo.setNickname(user.getNickname());
                userInfo.setUpdateTime(new Date());
                userInfo.setOpenid(user.getOpenid());
                userInfo.setHeadimgurl(user.getHeadimgurl());
                userInfo.setProvince(user.getProvince());
                userInfo.setCoin(0);
                userInfo.setSex(user.getSex() == null ? 1 : user.getSex());
                userInfo.setType(ApiConstant.UserType.GENNERAL_USER);
                userInfoMapper.insert(userInfo);
                //在用户经验值的表中插入一条该用户的数据
                createUserEmpirical(userId);
                return userId;
            }
            return userInfos.get(0).getUserId();
        }
        return null;
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
                userInfo.setUserId(userId);
                userInfo.setType(ApiConstant.UserType.GENNERAL_USER);
                userInfo.setCoin(0);
                userInfo.setCreateTime(new Date());
                userInfo.setUpdateTime(new Date());
                userInfoMapper.insert(userInfo);
                createUserEmpirical(userInfo.getUserId());
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
        //阅币同步
        UserInfo userInfo = getUserInfoByLocal(userId);
        int coin = userInfo.getCoin();
        UserInfo usup = new UserInfo();
        usup.setCoin(coin);
        UserInfoExample example = new UserInfoExample();
        example.createCriteria().andUserIdEqualTo(userId);
        userInfoMapper.updateByExampleSelective(usup, example);
        //清除缓存
        redisService.hmSet("userInfo", userId, "");
    }

    @Override
    public Map<String, Object> getUserInfo(String userId) {
        Map<String, Object> map = new HashMap<>();
        //首先从缓存中取看能不能取到
        String isExists = (String) redisService.hmGet("userInfo", userId);
        if (StringUtils.isNotEmpty(isExists)) {
            clearUserByRedis(userId);//同步redis和mysql中的数据
        }
        UserInfo userInfo = getUserInfoByLocal(userId);
        //获得用户的经验值
        UserEmpiricalExample example = new UserEmpiricalExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<UserEmpirical> empiricals = userEmpiricalMapper.selectByExample(example);
        if (empiricals != null && empiricals.size() > 0) {
            int empircal = empiricals.get(0).getEmpirical();
            map.put("empircal", empircal);
            map.put("grade", empiricals.get(0).getGrade());
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
                userInfo.setNickname(user.getNickname());
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

}
