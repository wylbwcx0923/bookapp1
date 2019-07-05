package com.jxtc.bookapp.utils;


import com.jxtc.bookapp.config.SmsConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 不忘初心
 * 本类是用于发送短信验证码的工具类
 */
public class SmsUtil {

    /**
     * 发送验证码的方法
     *
     * @param config 发送消息的配置信息
     * @param to     发送给用户的手机号
     * @return
     */
    public static boolean sendCode(SmsConfig config, String to, String smsContent) {
        //构建调用短信接口的参数
        Map<String, String> params = new HashMap();
        params.put("respDataType", config.getRespDataType());
        params.put("accountSid", config.getAccountSid());
        //发送的是构建的文本内容
        params.put("smsContent", smsContent);
        //发送给哪个用户
        params.put("to", to);
        //设置时间戳
        String timestamp = TimeUtil.getTimestamp();
        params.put("timestamp", timestamp);
        //签名
        //构建签名
        String str = config.getAccountSid() + config.getAuthToken() + timestamp;
        String sig = DigestUtils.md5DigestAsHex(str.getBytes());
        params.put("sig", sig);
        //短信类型
        params.put("smsType", config.getSmsType());
        //调用短信发送接口
        System.out.println(params.get("smsContent"));
        String res = HttpClientUtil.doPost(config.getUrl(), params);
        JSONObject result = JSONObject.fromObject(res);
        System.out.println(result);
        String respCode = result.getString("respCode");
        JSONArray failList = result.getJSONArray("failList");
        if ("0000".equals(respCode)) {
            //请求成功
            if (failList == null || failList.size() == 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}
