package com.jxtc.bookapp.service;

import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface WxPayService {
    /**
     * 统一下单和生成订单
     *
     * @param productId
     * @param totalFee
     * @param userId
     * @param couponId
     * @return
     */
    Map<String, Object> createOrder(HttpServletRequest request, int productId, double totalFee, String userId, Integer bookId, Integer couponId);

    /**
     * 订单处理
     *
     * @param orderId
     */
    @Transactional
    void orderProcess(String orderId);

    /**
     * 判断支付结果
     *
     * @param orderId
     * @return
     */
    boolean payResult(String orderId);

    /**
     * 微信支付中签约
     *
     * @param request
     * @param productId
     * @param totalFee
     * @param userId
     * @param bookId
     * @param couponId
     * @return
     */
    Map<String, Object> payContract(HttpServletRequest request, int productId, double totalFee, String userId, Integer bookId, Integer couponId);

    /**
     * 微信自动扣费纯签约接口
     *
     * @param productId
     * @param userId
     * @param bookId
     * @param couponId
     * @return
     */
    Map<String, Object> pureSign(int productId, double totalFee, String userId, Integer bookId, Integer couponId);


    void vipOrderProcess(Map<String, Object> restmap);

    @Transactional
    void wxPayrenew(Map<String, Object> restmap);
}
