package com.jxtc.bookapp.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 不忘初心
 */
public class PayUtil {
    /**
     * 获取随机字符串
     *
     * @return
     * @author
     */
    public static String create_nonce_str() {
        String s = UUID.randomUUID().toString();
        // 去掉“-”符号
        return s.replaceAll("\\-", "").toUpperCase();
    }

    /**
     * 获取时间戳
     *
     * @return
     * @author
     */
    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    /**
     * 获取ip
     *
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = request.getHeader("X-Requested-For");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0];
    }

    /**
     * 构造签名
     *
     * @param params
     * @param encode
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createSign(Map<String, Object> params, boolean encode) throws UnsupportedEncodingException {
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = value.toString();
            }
            if (encode) {
                temp.append(URLEncoder.encode(valueString, "UTF-8"));
            } else {
                temp.append(valueString);
            }
        }
        return temp.toString();
    }

    /**
     * 构造签名
     *
     * @param params
     * @param paternerKey
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String sign(Map<String, Object> params, String paternerKey) throws UnsupportedEncodingException {
        String string1 = createSign(params, false);
        String stringSignTemp = string1 + "&key=" + paternerKey;
        //String signValue = MD5Tools.encode(stringSignTemp).toUpperCase();
        String signValue = DigestUtils.md5Hex(stringSignTemp).toUpperCase();
        return signValue;
    }

    /**
     * 构造package, 这是我见到的最草蛋的加密，尼玛文档还有错
     *
     * @param params
     * @param paternerKey
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String packageSign(Map<String, Object> params, String paternerKey) throws UnsupportedEncodingException {
        String string1 = createSign(params, false);
        String stringSignTemp = string1 + "&key=" + paternerKey;
        return stringSignTemp;
    }

    /**
     * 生成订单id
     *
     * @return
     */
    public static String createOrderId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return "r" + sdf.format(new Date());
    }


    /**
     * 将字符流文件转化为字符串输出
     *
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String inputStream2String(InputStream in, String encoding) throws Exception {
        StringBuffer out = new StringBuffer();
        InputStreamReader reader = new InputStreamReader(in, encoding);
        char[] buff = new char[4096];
        for (int n; (n = reader.read(buff)) != -1; ) {
            out.append(new String(buff, 0, n));
        }
        return out.toString();
    }

}
