package com.jxtc.bookapp.service;

/**
 * 短信验证服务
 */
public interface SmsService {

    /**
     * 根据手机号码发送验证码
     *
     * @param phoneNumber
     */
    boolean sendCode(String phoneNumber);

}
