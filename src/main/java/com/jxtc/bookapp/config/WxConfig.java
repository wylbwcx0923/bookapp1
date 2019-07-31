package com.jxtc.bookapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author 不忘初心
 * 本类用于加载微信开发的配置文件
 */
@Configuration
@PropertySource(value = {"classpath:application-wxconfig.properties"})
@ConfigurationProperties(prefix = "wx")
public class WxConfig {
    private String appid;//移动应用的唯一标识
    private String secret;
    private String appsign;
    private String mchid;//微信商户号
    private String notifyurl;//异步通知地址
    private String paternerKey;
    private String mchidVip;//vip充值的商户号
    private String contractNotifyUrl;//签约成功的通知地址
    private String paternerKeyVip;
    private String papNotifyUrl;//微信自动扣费结果异步通知

    public String getPaternerKeyVip() {
        return paternerKeyVip;
    }

    public void setPaternerKeyVip(String paternerKeyVip) {
        this.paternerKeyVip = paternerKeyVip;
    }

    public String getContractNotifyUrl() {
        return contractNotifyUrl;
    }

    public void setContractNotifyUrl(String contractNotifyUrl) {
        this.contractNotifyUrl = contractNotifyUrl;
    }

    public String getPaternerKey() {
        return paternerKey;
    }

    public void setPaternerKey(String paternerKey) {
        this.paternerKey = paternerKey;
    }

    public String getAppid() {
        return appid;
    }

    public String getMchidVip() {
        return mchidVip;
    }

    public void setMchidVip(String mchidVip) {
        this.mchidVip = mchidVip;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAppsign() {
        return appsign;
    }

    public void setAppsign(String appsign) {
        this.appsign = appsign;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getNotifyurl() {
        return notifyurl;
    }

    public void setNotifyurl(String notifyurl) {
        this.notifyurl = notifyurl;
    }

    public String getPapNotifyUrl() {
        return papNotifyUrl;
    }

    public void setPapNotifyUrl(String papNotifyUrl) {
        this.papNotifyUrl = papNotifyUrl;
    }
}
