package com.jxtc.bookapp.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.jxtc.bookapp.config.AliPayConfig;
import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.service.AliPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author 不忘初心
 */
@RestController
@Api(tags = "支付宝支付接口", value = "支付宝支付接口")
@RequestMapping("jxapp/alipay")
public class AliPayController {

    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private AliPayConfig aliPayConfig;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "createOrder", method = RequestMethod.GET)
    @ApiOperation(value = "支付宝下单和签约接口", notes = "支付宝下单和签约接口", httpMethod = "GET")
    @ResponseBody
    public JXResult aliPayCreateOrder(@ApiParam(value = "产品Id", required = true)
                                      @RequestParam(value = "productId", defaultValue = "", required = false) int productId,
                                      @ApiParam(value = "金额(单位:元,保留两位小数)", required = true)
                                      @RequestParam(value = "totalFee", defaultValue = "", required = false) double totalFee,
                                      @ApiParam(value = "用户Id", required = true)
                                      @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                                      @ApiParam(value = "书籍Id", required = false)
                                      @RequestParam(value = "bookId", required = false) Integer bookId,
                                      @ApiParam(value = "优惠券Id", required = false)
                                      @RequestParam(value = "couponId", required = false) Integer couponId) {
        String orderStr = aliPayService.aliPayCreateOrder(productId, totalFee, userId, bookId, couponId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "支付宝下单成功", orderStr);
    }

    @RequestMapping(value = "notify", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "支付宝支付异步通知接口", notes = "支付宝支付异步通知接口")
    public String aliPayNotify(HttpServletRequest request) {
        logger.info("支付宝支付回调");
        //定义Map接收返回的参数
        Map<String, String> params = new HashMap<>();
        //通过支付宝的请求获得返回的信息
        Map<String, String[]> requestParameters = request.getParameterMap();
        //遍历Map取得各个参数及对应的值
        for (Iterator iter = requestParameters.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = requestParameters.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //商户的订单号
        String orderId = request.getParameter("out_trade_no");
        //订单的内容
        String orderBody = request.getParameter("body");
        //获得交易状态
        String tradeStatus = request.getParameter("trade_status");
        //获得签名校验
        boolean flag = false;
        try {
            //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
            flag = AlipaySignature.rsaCheckV1(params, aliPayConfig.getPublicKey(), aliPayConfig.getCharset(), "RSA2");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //订单处理
        String res=aliPayService.orderProcess(orderId, orderBody, tradeStatus, flag);
        return res;
    }

}
