package com.jxtc.bookapp.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * 本类是处理谷歌支付成功的回调的类
 */
public interface GooglePayService {

    /**
     * 处理订单
     *
     * @param userId
     * @param productId
     * @return
     */
    @Transactional
    boolean handleGooglePayOrder(String userId, Integer productId);

}
