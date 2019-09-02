package com.jxtc.bookapp.utils;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.WxConfig;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.app.*;
import com.jxtc.bookapp.service.CanalPopularizeService;
import com.jxtc.bookapp.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 不忘初心
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
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private WxConfig config;
    @Autowired
    private CanalPopularizeService canalPopularizeService;
    @Autowired
    private CanalPopularizeCountMapper canalPopularizeCountMapper;

    /**
     * 该方法为定时执行任务
     * 用于自动扣费和会员自动过期
     */
    @Scheduled(cron = "0 */1 * * * ?")//每1分钟钟执行一次
    @Transactional
    public void vipExpire() {
        String port = getLocalPort();
        if (!"9991".equals(port)) {
            //只在正式环境的其中一个服务器中执行定时任务
            return;
        }
        logger.info("执行了会员过期方法");
        //查找没有过期的所有VIP用户
        UserVipExample example = new UserVipExample();
        //查询扣款时间在现在之前,说明需要扣款了
        example.createCriteria().andStatusEqualTo(ApiConstant.VipStatus.DREDGE).andPayTimeLessThanOrEqualTo(new Date());
        List<UserVip> userVips = userVipMapper.selectByExample(example);
        if (userVips != null && userVips.size() > 0) {
            System.out.println("需要扣费的用户为:" + userVips);
            for (UserVip userVip : userVips) {
                //调用微信 扣款接口进行扣费
                //构造请求参数
                Map<String, Object> params = new HashMap<>();
                String appid = config.getAppid();//微信方APP的唯一标识
                String mchId = config.getMchidVip();//自动扣费的微信商户号
                String nonceStr = PayUtil.create_nonce_str();//随机字符串
                String body = "会员续费扣款";//商品或支付单简要描述
                String orderId = PayUtil.createOrderId();//商户方的订单Id
                int totalFee = userVip.getTotalFee();//订单总金额，单位为分
                //调用微信支付API的机器IP
                String spbillCreateIp = null;
                try {
                    spbillCreateIp = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                String notifyUrl = config.getPapNotifyUrl();//扣款结果异步通知
                String tradeType = "PAP";//交易类型PAP-微信委托代扣支付
                String contractId = userVip.getContractId();//签约成功后，微信返回的委托代扣协议id
                params.put("appid", appid);
                params.put("mch_id", mchId);
                params.put("nonce_str", nonceStr);
                params.put("body", body);
                params.put("out_trade_no", orderId);
                params.put("total_fee", totalFee);
                params.put("spbill_create_ip", spbillCreateIp);
                params.put("notify_url", notifyUrl);
                params.put("trade_type", tradeType);
                params.put("contract_id", contractId);
                //构建签名
                try {
                    String sign = PayUtil.sign(params, config.getPaternerKeyVip());
                    params.put("sign", sign);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String paramsXml = XmlUtil.toXml(params);
                String resultXml = HttpClientUtil.doPostXml(ApiConstant.WXPayConfig.PAY_PAPPAYAPPLY, paramsXml);
                Map<String, Object> result = XmlUtil.parseXml(resultXml);
                Set<String> keySet = result.keySet();
                for (String key : keySet) {
                    System.out.println("微信扣款接口调用结果:" + key + "\t" + result.get(key));
                }
                //订单入库
                Order order = new Order();
                if ("SUCCESS".equals(result.get("result_code"))) {
                    //扣款请求成功
                    order.setUpdateTime(new Date());
                    order.setOrderId(orderId);
                    order.setDes("自动续费请求扣款中...");
                    order.setCreateTime(new Date());
                    order.setStatus(ApiConstant.OrderStatus.DOWN_ORDER);
                    order.setUserId(userVip.getUserId());
                    order.setBookId(0);
                    int orderType = 0;
                    //获得用户的会员类型
                    switch (userVip.getVipType()) {
                        case ApiConstant.UserType.VIP_MONTH_USER:
                            //包月会员
                            orderType = ApiConstant.OrderType.VIP_MONTH_ORDER;
                            break;
                        case ApiConstant.UserType.VIP_QUARTER_USER:
                            //包季会员
                            orderType = ApiConstant.OrderType.VIP_QUARTER_ORDER;
                            break;
                        case ApiConstant.UserType.VIP_YEAR_USER:
                            //包年会员
                            orderType = ApiConstant.OrderType.VIP_YEAR_ORDER;
                            break;
                    }
                    order.setOrderType(orderType);
                    order.setAmount(totalFee);
                    orderMapper.insertSelective(order);
                } else {
                    //如果扣款失败,那么将用户的Vip状态改为取消签约
                    UserVip vipUp = new UserVip();
                    vipUp.setId(userVip.getId());
                    vipUp.setStatus(ApiConstant.VipStatus.EXPIRE);
                    userVipMapper.updateByPrimaryKeySelective(vipUp);
                }
            }
        }
        //过期掉那些续费失败的过期的会员
        UserVipExample vipExample = new UserVipExample();
        //没有过期,但是过期时间小于等于当前时间的用户需要过期
        vipExample.createCriteria().andStatusEqualTo(ApiConstant.VipStatus.EXPIRE).andExpireTimeLessThanOrEqualTo(new Date()).andContractIdNotEqualTo("0");
        List<UserVip> expireUsers = userVipMapper.selectByExample(vipExample);
        if (expireUsers != null && expireUsers.size() > 0) {
            logger.info("有用户需要过期");
            UserInfoExample userInfoExample = new UserInfoExample();
            for (UserVip expireUser : expireUsers) {
                userInfoExample.createCriteria().andUserIdEqualTo(expireUser.getUserId());
                UserInfo userUp = new UserInfo();
                userUp.setUpdateTime(new Date());
                userUp.setType(ApiConstant.UserType.GENNERAL_USER);//过期之后修改为普通用户
                userInfoService.clearUserByRedis(expireUser.getUserId());
                userInfoMapper.updateByExampleSelective(userUp, userInfoExample);
                UserVip userVipUp = new UserVip();
                userVipUp.setId(expireUser.getId());
                userVipUp.setContractId("0");
                userVipMapper.updateByPrimaryKeySelective(userVipUp);//将扣款凭证置空
                logger.info("过期了:" + expireUser.getUserId());
            }
        }
    }

    /**
     * 获取当前服务的端口号
     *
     * @return
     * @author 不忘初心
     */
    private String getLocalPort() {
        MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objectNames = null;
        try {
            objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        String port = objectNames.iterator().next().getKeyProperty("port");
        return port;
    }

    @Scheduled(cron = "0 0 0 */1 * ?") //每天o:oo分执行
    public void addCanalPopularizeCount() {
        String port = getLocalPort();
        if (!"9991".equals(port)) {
            //只在正式环境的其中一个服务器中执行定时任务
            return;
        }
        logger.info("执行了插入渠道推广统计");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String oneDay = sdf.format(new Date());
        List<Canal> canals = canalPopularizeService.getCanalList();
        if (canals != null && canals.size() > 0) {
            for (Canal canal : canals) {
                CanalPopularizeCount count = new CanalPopularizeCount();
                count.setOneDay(oneDay);
                count.setNounId(canal.getId());
                count.setDownCount(0);
                count.setPayCount(0);
                canalPopularizeCountMapper.insertSelective(count);
            }
        }
    }

}
