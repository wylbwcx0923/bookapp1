package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.app.*;
import com.jxtc.bookapp.service.GooglePayService;
import com.jxtc.bookapp.service.UserEmpiricalService;
import com.jxtc.bookapp.service.UserInfoService;
import com.jxtc.bookapp.utils.PayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value = "googlePayService")
public class GooglePayServiceImpl implements GooglePayService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserCoinMapper userCoinMapper;
    @Autowired
    private UserEmpiricalService userEmpiricalService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserVipMapper userVipMapper;
    @Autowired
    private OrderMapper orderMapper;


    @Override
    public boolean handleGooglePayOrder(String userId, Integer productId) {
        //程序执行至此,说明支付成功
        //1.通过商品Id查询出用户充值的产品
        Product product = productMapper.selectByPrimaryKey(productId);
        //2.判断订单的类型
        //生成订单Id
        String orderId = PayUtil.createOrderId();
        //定义订单类型
        int orderType = 0;
        switch (productId) {
            case 8: case 9:
            case 10: case 11:
                //程序执行至此,说明是普通充值(普通充值就是给阅币)
                //修改用户的阅币
                userCoinMapper.addCoinByUserId(userId, product.getOriginal() + product.getGift());
                orderType = 1;
                break;
            case 12: case 13:
            case 14:
                orderType = productId - 10;
                //程序执行至此,说明是VIP充值,修改用户的类型
                UserInfoExample example = new UserInfoExample();
                example.createCriteria().andUserIdEqualTo(userId);
                UserInfo userInfoUp = new UserInfo();
                userInfoUp.setType(product.getGift());
                userInfoMapper.updateByExampleSelective(userInfoUp, example);
                //Vip订单的处理
                UserVip userVip = new UserVip();
                userVip.setContractCode(orderId);
                userVip.setUserId(userId);
                userVip.setVipType(orderType);
                userVip.setStatus(2);
                userVip.setTotalFee(88);
                userVip.setContractId("Google");
                userVip.setSignTime(new Date());
                userVip.setPayTime(new Date());
                //获取到过期时间
                Long expireTimeLong = System.currentTimeMillis() + (product.getOriginal() * 24 * 60 * 60 * 1000L);
                userVip.setExpireTime(new Date(expireTimeLong));
                //插入Vip用户表中
                userVipMapper.insertSelective(userVip);
                break;
            default:
                return false;
        }
        //用户经验值的更新和升级
        int empirical = product.getProductPrice().intValue() * 10;
        userEmpiricalService.upgrade(userId, empirical);
        userInfoService.clearUserByRedis(userId);
        //创建一个订单,通知管理后台支付成功
        Order order = new Order();
        order.setBookName("充值页充值");
        order.setUserId(userId);
        order.setOrderId(orderId);
        order.setOrderType(orderType);
        int amount = (int) (product.getProductPrice() * 100);
        order.setAmount(amount);
        order.setBookId(0);
        order.setDes("用户通过谷歌支付,充值成功,充值金额为美元");
        order.setStatus(1);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.insertSelective(order);
        return true;
    }
}
