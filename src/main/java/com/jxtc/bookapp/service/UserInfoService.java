package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.UserInfo;
import com.jxtc.bookapp.mapper.app.UserCount;
import com.jxtc.bookapp.utils.PageResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface UserInfoService {
    /**
     * 微信登录
     *
     * @param code
     * @param canalId
     * @return
     */
    @Transactional
    String wxLogin(String code, Integer canalId);

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
     *
     * @param userInfo
     */
    void updateUser(UserInfo userInfo);

    /**
     * 绑定微信
     *
     * @param userId
     * @param code
     */
    void bindWeiXin(String userId, String code);

    /**
     * 绑定QQ
     *
     * @param userId
     * @param userQQ
     */
    void bindQQ(String userId, UserInfo userQQ);

    /**
     * 验证手机号和验证码
     *
     * @param phoneNumber
     * @param code
     * @param canalId
     * @return
     */
    @Transactional
    Map<String, Object> smsVerify(String phoneNumber, String code, Integer canalId);

    /**
     * 获得用户列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<UserInfo> getUserList(int pageIndex, int pageSize, String userId, String startTime, String endTime);

    /**
     * 赠送给用户阅币
     *
     * @param userId
     * @param coin
     */
    void sendCoin(String userId, int coin);

    //重置用户
    void verbUser(String userId);

    //用户留存统计
    Map<String, Object> userKeepCount();

    /**
     * 每日的用户留存
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageResult<UserCount> getUserCounts(int pageIndex, int pageSize);

    /**
     * 获得用户次日,7日,30日的留存
     *
     * @return
     */
    Map<String, Object> getUserKeepOneSevenAndMounth();

    /**
     * 谷歌用户登录
     *
     * @return
     */
    String googleLogin(String googleId,String nickname,String photo);
}
