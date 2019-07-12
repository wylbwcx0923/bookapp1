package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.WxConfig;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.OrderMapper;
import com.jxtc.bookapp.mapper.ProductMapper;
import com.jxtc.bookapp.mapper.UserCoinMapper;
import com.jxtc.bookapp.service.*;
import com.jxtc.bookapp.utils.HttpClientUtil;
import com.jxtc.bookapp.utils.PayUtil;
import com.jxtc.bookapp.utils.XmlUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信支付服务
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private WxConfig config;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserCoinMapper userCoinMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserEmpiricalService userEmpiricalService;
    @Autowired
    private UserCouponService userCouponService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public Map<String, Object> createOrder(HttpServletRequest request, int productId, double totalFee, String userId, Integer bookId, Integer couponId) {
        Map<String, Object> params = new HashMap<>();
        String appId = config.getAppid();//微信应用的唯一标识(必传参数)
        String mchId = config.getMchid();//微信商户号(必传参数)
        String nonceStr = PayUtil.create_nonce_str();//随机字符串(必传参数)
        String signType = "MD5";//签名方式(选传参数)
        Product product = productMapper.selectByPrimaryKey(productId);
        String body = product.getProductDes();//商品描述(必传参数)
        String detail = product.getProductInfo();//商品详情(选传参数)
        String orderId = PayUtil.createOrderId();//订单id(必传参数)
        int money = (int) (totalFee * 100);//支付总金额,单位是分(必传参数)
        String spbillIp = PayUtil.getIp(request);//调用微信支付API的机器IP(必传参数)
        String notifyUrl = config.getNotifyurl();//支付成功或者失败,异步通知的地址(必传参数)
        String tradeType = "APP";
        params.put("appid", appId);
        params.put("mch_id", mchId);
        params.put("nonce_str", nonceStr);
        params.put("sign_type", signType);
        params.put("body", body);
        params.put("detail", detail);
        params.put("out_trade_no", orderId);
        params.put("total_fee", money);
        params.put("spbill_create_ip", spbillIp);
        params.put("notify_url", notifyUrl);
        params.put("trade_type", tradeType);
        try {
            String sign = PayUtil.sign(params, config.getPaternerKey());//生成签名(必传参数)
            System.out.println("签名为:" + sign);
            params.put("sign", sign);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String xmlParams = XmlUtil.toXml(params);
        String xmlRes = HttpClientUtil.doPostXml(ApiConstant.WXPayConfig.CREATE_ORDER, xmlParams);
        Map<String, Object> result = XmlUtil.parseXml(xmlRes);
        //通过解析的结果来将订单入库
        Order order = new Order();
        if (result != null && result.size() > 0) {
            if (result.get("result_code") != null && "SUCCESS".equals(result.get("result_code"))) {
                //程序执行至此说明下单成功
                //保存用户的订单信息
                order.setOrderId(orderId);//用户的订单Id
                order.setStatus(ApiConstant.OrderStatus.DOWN_ORDER);//将订单状态设置为下单状态
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                order.setDes("用户已于" + sdf.format(new Date()) + "通过微信支付下单,购买商品为" + product.getProductName() + ",等待支付");
                order.setAmount(money);//设置该订单的总金额
                order.setUserId(userId);//下单用户
                order.setBookId(bookId);//设置用户是那本书下的单,如果 直接通过充值页面进入,传0
                order.setOrderType(productId);
                if (couponId != null && couponId != 0) {
                    //本次充值使用了优惠券
                    //将优惠券的ID放入缓存中,如果支付成功,那么需要将该优惠券状态变为已使用
                    redisService.set("COUPON_" + orderId, couponId + "");
                }
                orderService.insertOrder(order);
            }
        }
        //微信支付二次签名
        //appid，partnerid，prepayid，noncestr，timestamp，package
        Map<String, Object> againSign = new HashMap<>();
        againSign.put("appid", result.get("appid"));//app的唯一标识
        againSign.put("partnerid", result.get("mch_id"));//商户号
        againSign.put("prepayid", result.get("prepay_id"));//微信返回的订单id
        againSign.put("noncestr", result.get("nonce_str"));//随机字符串
        againSign.put("timestamp", PayUtil.create_timestamp());//时间戳
        againSign.put("package", "Sign=WXPay");//固定参数包名
        String sign = null;
        try {
            //进行微信二次签名
            sign = PayUtil.sign(againSign, config.getPaternerKey());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        againSign.put("sign", sign);
        againSign.put("result_code", result.get("result_code"));
        //返回本次支付的订单号,后期判断是否支付成功
        againSign.put("orderId", orderId);
        logger.info("返回的签名为:" + againSign.get("sign"));
        return againSign;
    }

    @Override
    public void orderProcess(String orderId) {
        //支付成功
        //修改订单状态
        OrderExample example = new OrderExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        Order order = new Order();
        order.setStatus(ApiConstant.OrderStatus.SUCCESS);
        order.setDes("该用户已于" + sdf.format(new Date()) + "使用微信,支付成功!");
        //获得成功订单的详情
        Order orderSuc = orderService.findOrderByOrderId(orderId);
        //因为订单的类型和产品的Id为一一对应的关系,所以我们通过订单的类型查询产品详情
        int productId = orderSuc.getOrderType();
        Product product = productMapper.selectByPrimaryKey(productId);
        switch (productId) {
            //以下四种订单定义为普通充值订单
            case ApiConstant.OrderType.GENNERAL_29:
            case ApiConstant.OrderType.GENNERAL_49:
            case ApiConstant.OrderType.GENNERAL_99:
            case ApiConstant.OrderType.GENNERAL_129:
                //用户充值成功,修改用户的阅币
                //修改用户的阅币
                UserCoinExample userCoinexample = new UserCoinExample();
                userCoinexample.createCriteria().andUserIdEqualTo(order.getUserId());
                List<UserCoin> userCoins = userCoinMapper.selectByExample(userCoinexample);
                UserCoin coin = userCoins.get(0);
                UserCoin userCoinUp = new UserCoin();
                userCoinUp.setCoin(coin.getCoin() + product.getOriginal() + product.getGift());
                userCoinMapper.updateByExampleSelective(userCoinUp, userCoinexample);
                break;
            //以下三种订单定义为VIP充值订单
            case ApiConstant.OrderType.VIP_MONTH_ORDER:
            case ApiConstant.OrderType.VIP_QUARTER_ORDER:
            case ApiConstant.OrderType.VIP_YEAR_ORDER:
                //此处代码暂缓

        }
        order.setUpdateTime(new Date());
        orderMapper.updateByExampleSelective(order, example);
        //用户经验值的更新和升级
        int empirical = product.getProductPrice().intValue() * 10;
        userEmpiricalService.upgrade(order.getUserId(), empirical);
        //判断本次充值是否使用了优惠券
        String isUse = (String) redisService.get("COUPON_" + orderId);
        if (StringUtils.isNotEmpty(isUse)) {
            //使用了优惠券
            userCouponService.useCouponById(Integer.valueOf(isUse));
            redisService.remove("COUPON_" + orderId);
        }
    }

    @Override
    public boolean payResult(String orderId) {
        Order order = orderService.findOrderByOrderId(orderId);
        if (order != null) {
            int status = order.getStatus();
            if (status != 0) {
                return true;
            }
        }
        return false;
    }


}
