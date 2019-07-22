package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.WxConfig;
import com.jxtc.bookapp.entity.*;
import com.jxtc.bookapp.mapper.*;
import com.jxtc.bookapp.mapper.app.*;
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
import java.util.*;

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
    @Autowired
    private UserVipMapper userVipMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserInfoService userInfoService;

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
        Order orderUp = new Order();
        orderUp.setStatus(ApiConstant.OrderStatus.SUCCESS);
        orderUp.setDes("该用户已于" + sdf.format(new Date()) + "使用微信,支付成功!");
        //获得成功订单的详情
        Order orderSuc = orderService.findOrderByOrderId(orderId);
        //因为订单的类型和产品的Id为一一对应的关系,所以我们通过订单的类型查询产品详情
        int productId = orderSuc.getOrderType();
        Product product = productMapper.selectByPrimaryKey(productId);
        //用户充值成功,修改用户的阅币
        //修改用户的阅币
        if (productId == ApiConstant.OrderType.GENNERAL_29
                || productId == ApiConstant.OrderType.GENNERAL_49
                || productId == ApiConstant.OrderType.GENNERAL_99
                || productId == ApiConstant.OrderType.GENNERAL_129) {
            //普通充值为修改阅币
            userCoinMapper.addCoinByUserId(orderSuc.getUserId(), product.getOriginal() + product.getGift());
        } else {
            //不是普通充值那就是vip充值
            System.out.println("本次为VIP充值");
            UserVip userVip = new UserVip();
            userVip.setContractCode(orderId);//设置签约协议号,需要在商户侧唯一
            userVip.setSignTime(new Date());//设置签约时间
            userVip.setUserId(orderSuc.getUserId());//设置用户Id
            userVip.setTotalFee(orderSuc.getAmount());//设置价格
            userVip.setStatus(ApiConstant.VipStatus.EXPIRE);//只是付款成功,这里只能先设置为未签约成功
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            UserInfoExample userInfoExample = new UserInfoExample();
            userInfoExample.createCriteria().andUserIdEqualTo(orderSuc.getUserId());
            UserInfo userUp = new UserInfo();
            if (productId == ApiConstant.OrderType.VIP_MONTH_ORDER) {
                //包月会员
                userVip.setVipType(ApiConstant.UserType.VIP_MONTH_USER);
                calendar.add(Calendar.DAY_OF_YEAR, 30);
                userUp.setType(ApiConstant.UserType.VIP_MONTH_USER);
            }
            if (productId == ApiConstant.OrderType.VIP_QUARTER_ORDER) {
                //包季会员
                userVip.setVipType(ApiConstant.UserType.VIP_QUARTER_USER);
                calendar.add(Calendar.DAY_OF_YEAR, 90);
                userUp.setType(ApiConstant.UserType.VIP_QUARTER_USER);
            }
            if (productId == ApiConstant.OrderType.VIP_YEAR_ORDER) {
                //包年会员
                userVip.setVipType(ApiConstant.OrderType.VIP_YEAR_ORDER);
                calendar.add(Calendar.DAY_OF_YEAR, 365);
                userUp.setType(ApiConstant.UserType.VIP_YEAR_USER);
            }
            userVip.setExpireTime(calendar.getTime());//设置过期时间
            //修改用户的类型
            userUp.setUpdateTime(new Date());
            userInfoMapper.updateByExampleSelective(userUp, userInfoExample);
            userInfoService.clearUserByRedis(orderSuc.getUserId());
            //将VIP入库
            userVipMapper.insertSelective(userVip);
        }
        orderUp.setUpdateTime(new Date());
        orderMapper.updateByExampleSelective(orderUp, example);
        //用户经验值的更新和升级
        int empirical = product.getProductPrice().intValue() * 10;
        userEmpiricalService.upgrade(orderSuc.getUserId(), empirical);
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

    @Override
    public Map<String, Object> payContract(HttpServletRequest request, int productId, double totalFee, String userId, Integer bookId, Integer couponId) {
        Map<String, Object> params = new HashMap<>();
        String appId = config.getAppid();//商户APP唯一标识
        String mchId = config.getMchidVip();//vip充值的商户号
        String orderId = PayUtil.createOrderId();//后台生成的订单
        String nonceStr = PayUtil.create_nonce_str();//随机字符串
        Product product = productMapper.selectByPrimaryKey(productId);
        String body = product.getProductDes();//商品描述(必传参数)
        String detail = product.getProductInfo();//商品详情(选传参数)
        String notifyUrl = config.getNotifyurl();//支付结果通知地址
        int money = (int) (totalFee * 100);//支付总金额,单位是分(必传参数)
        String spbillIp = PayUtil.getIp(request);//调用微信支付API的机器IP(必传参数)
        String tradeType = "APP";//交易类型
        int planId = product.getOriginal();//商户签约的模板Id
        String contractCode = orderId;//签约协议号,需要在商户侧唯一
        String requestSerial = PayUtil.create_timestamp();//商户请求签约时的序列号，要求唯一性
        String contractDisplayAccount = userId;//签约用户的名称,用于页面展示
        String contractNotifyUrl = config.getContractNotifyUrl();//签约成功的回调
        params.put("appid", appId);
        params.put("mch_id", mchId);
        params.put("contract_mchid", mchId);
        params.put("contract_appid", appId);
        params.put("out_trade_no", orderId);
        params.put("nonce_str", nonceStr);
        params.put("body", body);
        params.put("detail", detail);
        params.put("notify_url", notifyUrl);//支付成功的回调
        params.put("total_fee", money);
        params.put("spbill_create_ip", spbillIp);
        params.put("trade_type", tradeType);
        params.put("plan_id", planId);
        params.put("contract_code", contractCode);
        params.put("request_serial", requestSerial);
        params.put("contract_display_account", contractDisplayAccount);
        params.put("contract_notify_url", contractNotifyUrl);//签约结果的回调
        try {
            String sign = PayUtil.sign(params, config.getPaternerKeyVip());//获得签名
            System.out.println("VIP签约得到的sign为:" + sign);
            params.put("sign", sign);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //进行请求,下单
        String paramsXml = XmlUtil.toXml(params);
        String xmlRes = HttpClientUtil.doPostXml(ApiConstant.WXPayConfig.PAY_CONTRACTORDER, paramsXml);
        //将下单和签约的结果解析成为Map
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
            sign = PayUtil.sign(againSign, config.getPaternerKeyVip());
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
    public Map<String, Object> pureSign(int productId, double totalFee, String userId, Integer bookId, Integer couponId) {
        Map<String, Object> params = new HashMap<>();
        String appid = config.getAppid();//app的唯一标识
        String mchId = config.getMchidVip();//微信自动扣费的商户号
        Product product = productMapper.selectByPrimaryKey(productId);
        String planId = product.getOriginal() + "";//微信签约时申请的模板Id
        String orderId = PayUtil.createOrderId();//生成商户的订单号
        String contractCode = orderId;//签约协议号,需要在商户侧唯一
        String requestSerial = PayUtil.create_timestamp();//商户请求签约时的序列号，要求唯一性
        String contractDisplayAccount = userId;//签约用户的名称,用于页面展示
        String contractNotifyUrl = config.getContractNotifyUrl();//签约成功的回调
        String version = "1.0";//版本号
        String timestamp = PayUtil.create_timestamp();//时间戳
        int returnApp = 3;//3表示返回app, 不填则不返
        params.put("appid", appid);
        params.put("mch_id", mchId);
        params.put("plan_id", planId);
        params.put("contract_code", contractCode);
        params.put("request_serial", requestSerial);
        params.put("contract_display_account", contractDisplayAccount);
        params.put("notify_url", contractNotifyUrl);
        params.put("version", version);
        params.put("timestamp", timestamp);
        params.put("returnApp", returnApp);
        //构建签名
        try {
            String sign = PayUtil.sign(params, config.getPaternerKeyVip());
            params.put("sign", sign);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //保存用户vip记录
        UserVip vip = new UserVip();
        //根据订单Id查询订单表
        int money = (int) (totalFee * 100);
        vip.setTotalFee(money);//设置每次的扣款金额
        vip.setUserId(userId);//设置签约的用户Id
        vip.setContractCode(orderId);//设置订单Id
        //判断vip的类型
        switch (productId) {
            case ApiConstant.OrderType.VIP_MONTH_ORDER:
                //包月会员
                vip.setVipType(ApiConstant.UserType.VIP_MONTH_USER);
                break;
            case ApiConstant.OrderType.VIP_QUARTER_ORDER:
                //包季会员
                vip.setVipType(ApiConstant.UserType.VIP_QUARTER_USER);
                break;
            case ApiConstant.OrderType.VIP_YEAR_ORDER:
                //包年会员
                vip.setVipType(ApiConstant.UserType.VIP_YEAR_USER);
                break;
        }
        if (couponId != null && couponId != 0) {
            //本次充值使用了优惠券
            //将优惠券的ID放入缓存中,如果支付成功,那么需要将该优惠券状态变为已使用
            redisService.set("COUPON_" + orderId, couponId + "");
        }
        params.put("result_code", "SUCCESS");
        userVipMapper.insertSelective(vip);
        return params;
    }

    @Override
    public void vipOrderProcess(Map<String, Object> restmap) {
        // 订单支付成功 业务处理
        String contractCode = (String) restmap.get("contract_code"); // 签约协议号(和我们的订单号是一个东西)
        String changeType = (String) restmap.get("change_type");//ADD--签约;DELETE--解约
        String contractId = (String) restmap.get("contract_id");//微信委托扣款的Id(重要)
        //首先通过签约时候的协议号(contractCode)找到该用户的vip
        UserVipExample example = new UserVipExample();
        example.createCriteria().andContractCodeEqualTo(contractCode);
        List<UserVip> userVips = userVipMapper.selectByExample(example);
        if (userVips == null) {
            return;
        }
        UserVip vip = userVips.get(0);
        //判断是签约还是解约
        if ("ADD".equals(changeType)) {
            vip.setStatus(ApiConstant.VipStatus.DREDGE);//开通成功
            vip.setContractId(contractId);
            //下一次的扣费时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(vip.getExpireTime());
            calendar.add(Calendar.DAY_OF_YEAR, -1);//过期的前一天扣费
            vip.setPayTime(calendar.getTime());//签约成功
        }
        if ("DELETE".equals(changeType)) {
            //此时为解约
            vip.setStatus(ApiConstant.VipStatus.EXPIRE);//解约成功
        }
        userVipMapper.updateByExampleSelective(vip, example);
    }

    @Override
    public void wxPayrenew(Map<String, Object> restmap) {
        String tradeState = (String) restmap.get("trade_state");//交易状态
        String orderId = (String) restmap.get("out_trade_no");//商户订单号
        String contractId = (String) restmap.get("contract_id");//微信委托扣款的Id
        //订单的信息
        Order order = orderService.findOrderByOrderId(orderId);
        //修改用户的类型
        UserInfo userUp = new UserInfo();
        //订单状态的更新
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andOrderIdEqualTo(orderId);
        Order orderUp = new Order();
        UserVipExample example = new UserVipExample();
        example.createCriteria().andContractIdEqualTo(contractId);
        List<UserVip> userVips = userVipMapper.selectByExample(example);
        UserVip vip = null;
        if (userVips == null) {
            return;
        }
        vip = userVips.get(0);
        UserVip vipUp = new UserVip();
        if ("SUCCESS".equals(tradeState)) {
            //订单成功,修改用户的VIP天数
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(vip.getExpireTime());
            //用户信息
            switch (order.getOrderType()) {
                case ApiConstant.OrderType.VIP_MONTH_ORDER:
                    //订单为包月,过期时间后延30天
                    calendar.add(Calendar.DAY_OF_YEAR, 30);
                    userUp.setType(ApiConstant.UserType.VIP_MONTH_USER);
                    break;
                case ApiConstant.OrderType.VIP_QUARTER_ORDER:
                    //订单为包季,过期时间后延90天
                    calendar.add(Calendar.DAY_OF_YEAR, 90);
                    userUp.setType(ApiConstant.UserType.VIP_QUARTER_USER);
                    break;
                case ApiConstant.OrderType.VIP_YEAR_ORDER:
                    //订单为包年,过期时间后延365天
                    calendar.add(Calendar.DAY_OF_YEAR, 365);
                    userUp.setType(ApiConstant.UserType.VIP_YEAR_USER);
                    break;
            }
            Date expireTime = calendar.getTime();
            vipUp.setExpireTime(expireTime);
            calendar.setTime(expireTime);
            calendar.add(Calendar.DAY_OF_YEAR, -1);//过期前一天扣费
            vipUp.setPayTime(calendar.getTime());
            vipUp.setStatus(ApiConstant.VipStatus.DREDGE);
            //用户经验值的更新和升级
            int empirical = order.getAmount() / 10;
            userEmpiricalService.upgrade(order.getUserId(), empirical);
            //订单状态的变化
            orderUp.setStatus(ApiConstant.OrderStatus.SUCCESS);
            orderUp.setDes("VIP订单付费成功");
            logger.info("VIP自动扣费成功,VIP续订成功");
        } else {
            vipUp.setStatus(ApiConstant.VipStatus.EXPIRE);
            orderUp.setStatus(ApiConstant.OrderStatus.FAIL);
            orderUp.setDes("VIP订单扣款失败,失败原因为:" + restmap.get("err_code_des"));
        }
        //用户Vip的状态
        userVipMapper.updateByExampleSelective(vipUp, example);
        //修改订单的状态
        orderUp.setUpdateTime(new Date());
        orderMapper.updateByExampleSelective(orderUp, orderExample);
    }

}
