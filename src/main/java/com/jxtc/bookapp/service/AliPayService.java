package com.jxtc.bookapp.service;


import org.springframework.transaction.annotation.Transactional;

public interface AliPayService {

    /**
     * 支付宝支付,初始化和下单操作
     *
     * @param productId
     * @param totalFee
     * @param userId
     * @param bookId
     * @param couponId
     * @return
     */
    String aliPayCreateOrder(int productId, double totalFee, String userId, Integer bookId, Integer couponId);

    /**
     * 处理订单的方法
     *
     * @param orderId
     * @param orderBody
     * @param tradeStatus
     * @param flag
     */
    @Transactional
    String orderProcess(String orderId, String orderBody, String tradeStatus, boolean flag);
}
