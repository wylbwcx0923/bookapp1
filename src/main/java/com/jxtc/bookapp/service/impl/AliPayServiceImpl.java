package com.jxtc.bookapp.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.jxtc.bookapp.config.AliPayConfig;
import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.OrderMapper;
import com.jxtc.bookapp.mapper.ProductMapper;
import com.jxtc.bookapp.mapper.UserCoinMapper;
import com.jxtc.bookapp.service.*;
import com.jxtc.bookapp.utils.PayUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Service
public class AliPayServiceImpl implements AliPayService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AliPayConfig aliPayConfig;
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

    @Override
    public String aliPayCreateOrder(int productId, double totalFee, String userId, Integer bookId, Integer couponId) {
        //初始化支付宝支付的客户端
        AlipayClient client = new DefaultAlipayClient(
                aliPayConfig.getUrl(),//正式环境的请求地址
                aliPayConfig.getAppId(),//移动应用的唯一标识
                aliPayConfig.getPrivateKey(),//商户私钥
                aliPayConfig.getFormat(),//响应格式
                aliPayConfig.getCharset(),//字符集
                aliPayConfig.getPublicKey(),//支付宝公钥
                aliPayConfig.getSignType()//签名类型
        );
        //实例化具体api对应的request类,类名称和接口名称对应,当前调用接口为:alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //支付宝的sdk中已经封装好了公共的参数,我们使用的时候只需要传入我们的业务参数即可
        //以下方法为sdk的model入参方法(model和biz_content同时存在的情况下去biz_content)
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        //获得用户充值的产品
        Product product = productMapper.selectByPrimaryKey(productId);
        model.setBody(product.getProductInfo());//设置产品的主体
        model.setSubject(aliPayConfig.getAppName());//设置应用的名称
        String orderId = PayUtil.createOrderId();//利用支付的工具类生成订单
        model.setOutTradeNo(orderId);//设置订单号
        model.setTimeoutExpress("60m");//设置支付的超时时间,当前设置为1分钟
        model.setTotalAmount(totalFee + "");//设置支付金额,单位是元
        model.setProductCode("QUICK_MSECURITY_PAY");//app支付固定参数
        request.setBizModel(model);//发送请求
        request.setNotifyUrl(aliPayConfig.getCallBack());//支付结果异步通知地址
        //以下使用客户端执行请求并接收支付宝的响应数据
        String orderString = null;
        try {
            AlipayTradeAppPayResponse response = client.sdkExecute(request);
            orderString = response.getBody();
            //程序执行至此说明下单成功
            //进行订单入库的操作
            Order order = new Order();
            order.setOrderId(orderId);//用户的订单Id
            order.setStatus(ApiConstant.OrderStatus.DOWN_ORDER);//将订单状态设置为下单状态
            order.setDes("用户已于" + sdf.format(new Date()) + "通过支付宝支付下单,购买商品为" + product.getProductName() + ",等待支付");
            int amount = (int) (totalFee * 100);
            order.setAmount(amount);//设置该订单的总金额
            order.setUserId(userId);//下单用户
            order.setBookId(bookId);//设置用户是那本书下的单,如果 直接通过充值页面进入,传0
            order.setOrderType(productId);
            if (couponId != null && couponId != 0) {
                //本次充值使用了优惠券
                //将优惠券的ID放入缓存中,如果支付成功,那么需要将该优惠券状态变为已使用
                redisService.set("COUPON_" + orderId, couponId + "");
            }
            orderService.insertOrder(order);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return orderString;
    }

    @Override
    public String orderProcess(String orderId, String orderBody, String tradeStatus, boolean flag) {
        String res = "fail";
        if (flag) {
            logger.debug("支付宝签名校验通过");
            //签名校验通过
            OrderExample example = new OrderExample();
            example.createCriteria().andOrderIdEqualTo(orderId);
            Order order = new Order();
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                res = "success";
                logger.debug("支付宝支付成功");
                //支付成功
                //修改订单状态
                order.setStatus(ApiConstant.OrderStatus.SUCCESS);
                order.setDes("该用户已于" + sdf.format(new Date()) + "使用支付宝,支付成功!");
                //获得成功订单的详情
                logger.info("支付宝支付成功的订单Id" + orderId);
                Order orderSuc = orderService.findOrderByOrderId(orderId);
                //因为订单的类型和产品的Id为一一对应的关系,所以我们通过订单的类型查询产品详情
                int productId = orderSuc.getOrderType();
                logger.info("用户购买的产品ID为:" + productId);
                Product product = productMapper.selectByPrimaryKey(productId);
                //用户充值成功,修改用户的阅币
                logger.info("使用支付宝付款的用户的信息为:" + order.getUserId());
                //修改用户的阅币
                userCoinMapper.addCoinByUserId(order.getUserId(), product.getOriginal() + product.getGift());
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
            } else {
                //修改订单状态
                order.setStatus(ApiConstant.OrderStatus.FAIL);
                order.setDes("支付失败!");
                logger.info("支付失败的订单");
                res = "fail";
            }
            order.setUpdateTime(new Date());
            orderMapper.updateByExampleSelective(order, example);
        } else {
            res = "fail";
            logger.info("支付宝签名检验失败");
        }
        return res;
    }


}
