package com.jxtc.bookapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author 不忘初心
 * 短信验证码的配置类
 */
@Configuration
@PropertySource(value = {"classpath:application-sms.properties"})
@ConfigurationProperties(prefix = "sms")
public class SmsConfig {
    private String respDataType;
    private String accountSid;
    private String authToken;
    private String smsType;
    private String smsContent;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRespDataType() {
        return respDataType;
    }

    public void setRespDataType(String respDataType) {
        this.respDataType = respDataType;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getSmsType() {
        return smsType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }
}
