package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.config.WxConfig;
import com.jxtc.bookapp.service.WxPayService;
import com.jxtc.bookapp.utils.PayUtil;
import com.jxtc.bookapp.utils.XmlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Api(value = "微信支付接口", tags = "微信支付接口")
@RestController
@RequestMapping("jxapp/wxpay")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private WxConfig wxConfig;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(value = "/unifiedorder", method = RequestMethod.GET)
    @ApiOperation(value = "微信统一下单接口", notes = "微信统一下单接口", httpMethod = "GET")
    @ResponseBody
    public JXResult wxCreateOrder(HttpServletRequest request,
                                  @ApiParam(value = "产品Id", required = true)
                                  @RequestParam(value = "productId", defaultValue = "", required = false) int productId,
                                  @ApiParam(value = "金额(单位:元,保留两位小数)", required = true)
                                  @RequestParam(value = "totalFee", defaultValue = "", required = false) double totalFee,
                                  @ApiParam(value = "用户Id", required = true)
                                  @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                                  @ApiParam(value = "书籍Id", required = false)
                                  @RequestParam(value = "bookId", defaultValue = "", required = false) Integer bookId,
                                  @ApiParam(value = "优惠券Id", required = false)
                                  @RequestParam(value = "couponId", defaultValue = "", required = false) Integer couponId) {
        Map<String, Object> order = wxPayService.createOrder(request, productId, totalFee, userId, bookId, couponId);
        if ("SUCCESS".equals(order.get("result_code"))) {
            return new JXResult(true, ApiConstant.StatusCode.OK, "微信下单成功!", order);
        }
        return new JXResult(false, ApiConstant.StatusCode.SERVERERROR, "微信下单失败");
    }

    @RequestMapping(value = "/notify", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "微信支付结果通知接口", notes = "微信支付结果通知接口")
    public void wxPayNotify(HttpServletRequest request, HttpServletResponse response) {
        //设置字符集
        response.setCharacterEncoding("UTF-8");
        //设置响应内容的主体为xml(现在也就微信还在用xml!!!)
        response.setContentType("text/xml");
        try {
            ServletInputStream in = request.getInputStream();
            String resxml = PayUtil.inputStream2String(in, "UTF-8");
            Map<String, Object> restmap = XmlUtil.parseXml(resxml);
            logger.info("支付结果通知：" + restmap);
            if ("SUCCESS".equals(restmap.get("result_code"))) {
                // 订单支付成功 业务处理
                String out_trade_no = (String) restmap.get("out_trade_no"); // 商户订单号
                // 通过商户订单判断是否该订单已经处理 如果处理跳过 如果未处理先校验sign签名 再进行订单业务相关的处理
                String sing = (String) restmap.get("sign"); // 返回的签名
                restmap.remove("sign");
                String signnow = PayUtil.sign(restmap, wxConfig.getPaternerKey());
                if (signnow.equals(sing)) {
                    // 进行业务处理
                    logger.info("订单支付通知：支付成功，订单号" + out_trade_no);
                    //记录订单成功
                    //根据订单编号处理业务
                    wxPayService.orderProcess(out_trade_no);
                    // 处理成功后响应xml
                    Map<String, String> respMap = new HashMap<>();
                    respMap.put("return_code", "SUCCESS"); //响应给微信服务器
                    respMap.put("return_msg", "OK");
                    String resXml = XmlUtil.toXml(restmap);
                    response.getWriter().write(resXml);
                } else {
                    logger.info("订单支付通知：签名错误");
                }
            } else {
                logger.info("订单支付通知：支付失败，" + restmap.get("err_code") + ":" + restmap.get("err_code_des"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ApiOperation(notes = "微信支付结果回调", value = "微信支付结果回调", httpMethod = "GET")
    public JXResult payResult(@ApiParam(value = "订单ID", required = true)
                              @RequestParam(value = "orderId", defaultValue = "", required = false) String orderId) {
        boolean flag = wxPayService.payResult(orderId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", flag);
    }
}
