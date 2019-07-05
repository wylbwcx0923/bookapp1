package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.SmsConfig;
import com.jxtc.bookapp.service.RedisService;
import com.jxtc.bookapp.service.SmsService;
import com.jxtc.bookapp.utils.SmsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SmsConfig smsConfig;
    @Autowired
    private RedisService redisService;


    @Override
    public boolean sendCode(String phoneNumber) {
        //生成6位数的验证码
        String code = createCode();
        //每次调用该方法,先初始化一下消息
        String smsContent = smsConfig.getSmsContent() + code + "，该验证码1分钟内有效。请勿泄漏于他人。";
        boolean flag = SmsUtil.sendCode(smsConfig, phoneNumber, smsContent);
        if (flag) {
            redisService.set("SMS_" + phoneNumber, code, ApiConstant.Timer.ONE_MIN);
            logger.info("消息发送成功,消息为:" + smsContent);
        } else {
            logger.info("消息发送失败");
        }
        return flag;
    }


    /**
     * 生成验证码
     *
     * @return
     */
    private String createCode() {
        Random random = new Random();
        //生成6位数的验证码
        int code = random.nextInt(900000) + 100000;
        return code + "";
    }
}
