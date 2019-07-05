package com.jxtc.bookapp.intercept;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wyl
 * 本类用于拦截所有的运行时异常
 */
@ControllerAdvice
public class ErrorIntercept {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(RuntimeException.class) //拦截所有运行时的全局异常
    @ResponseBody //返回 JOSN
    public JXResult ErrorTest(Exception e) {
        logger.error("Unexpected error in ", e);
        return new JXResult(false, ApiConstant.StatusCode.SERVERERROR,
                "服务器开小差了,快去禀报我的开发者吧!");
    }
}
