package com.jxtc.bookapp.intercept;


import com.alibaba.fastjson.JSON;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.jxtc.bookapp.config.JXResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 不忘初心
 * 本类是响应的拦截类,对响应做统一处理
 */
@ControllerAdvice(basePackages = "com.jxtc.bookapp.controller")
public class ApiControllerAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private HttpServletRequest request;

    /**
     * 判断是否拦截该请求的响应
     *
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        //从请求的参数中获取是否转化为繁体的参数
        String isComplex = request.getParameter("isComplex");
        if (StringUtils.isNotBlank(isComplex) && isComplex.equals("1")) {
            return true;
        }
        return false;
    }

    /**
     * 响应一旦被拦截就会执行这个方法
     *
     * @param body
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    @ResponseBody
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType
            , Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest
            , ServerHttpResponse serverHttpResponse) {
        if (body instanceof JXResult) {
            //得到的响应结果
            JXResult result = (JXResult) body;
            //将返回结果的中文转化为繁体
            String resultStr = ZhConverterUtil.convertToTraditional(JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss"));
            return JSON.parseObject(resultStr);
        }
        return body;
    }
}
