package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.UserInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface UserInfoService {
    /**
     * 微信登录
     *
     * @param code
     * @return
     */
    @Transactional
    String wxLogin(String code);

    /**
     * qq登录
     *
     * @param userInfo
     * @return
     */
    @Transactional
    String qqLogin(UserInfo userInfo);

    /**
     * 从本地获得用户信息
     *
     * @param userId
     * @return
     */
    UserInfo getUserInfoByLocal(String userId);

    /**
     * 将用户从redis中清除
     *
     * @param userId
     */
    void clearUserByRedis(String userId);


    /**
     * 获得用户信息全部信息
     *
     * @param userId
     * @return
     */
    Map<String, Object> getUserInfo(String userId);

    /**
     * 修改用户的基本信息
     * @param userInfo
     */
    void updateUser(UserInfo userInfo);

    /**
     * 绑定微信
     * @param userId
     * @param code
     */
    void bindWeiXin(String userId,String code);

    /**
     * 绑定QQ
     * @param userId
     * @param userQQ
     */
    void bindQQ(String userId,UserInfo userQQ);

    /**
     * 验证手机号和验证码
     *
     * @param phoneNumber
     * @param code
     * @return
     */
    @Transactional
    Map<String,Object> smsVerify(String phoneNumber, String code);
}
