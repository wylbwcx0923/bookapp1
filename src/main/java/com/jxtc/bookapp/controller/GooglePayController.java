package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.service.GooglePayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "谷歌支付接口", value = "谷歌支付接口")
@RestController
@RequestMapping("/jxapp/google")
public class GooglePayController {

    @Autowired
    private GooglePayService googlePayService;

    @ApiOperation(value = "谷歌支付订单处理", httpMethod = "POST")
    @RequestMapping(value = "pay/handle", method = RequestMethod.POST)
    public JXResult handleGooglePay(@ApiParam(value = "用户Id", defaultValue = "", required = true)
                                    @RequestParam(value = "userId", required = true) String userId,
                                    @ApiParam(value = "商品Id", defaultValue = "", required = true)
                                    @RequestParam(value = "productId", required = true) Integer productId) {
        boolean flag = googlePayService.handleGooglePayOrder(userId, productId);
        JXResult jxResult = null;
        if (flag) {
            jxResult = new JXResult(true, ApiConstant.StatusCode.OK, "充值成功");
        } else {
            jxResult = new JXResult(false, ApiConstant.StatusCode.ERROR, "充值失败");
        }
        return jxResult;
    }

}
