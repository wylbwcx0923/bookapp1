package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "短信验证接口", value = "短信验证接口")
@RequestMapping("/jxapp/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @ApiOperation(value = "获取验证码", notes = "获取验证码", httpMethod = "GET")
    @RequestMapping(value = "send", method = RequestMethod.GET)
    public JXResult addReward(@ApiParam(value = "手机号码", required = true)
                              @RequestParam(value = "phoneNumber", defaultValue = "", required = true) String phoneNumber) {
        boolean isSuccess = smsService.sendCode(phoneNumber);
        if (isSuccess) {
            return new JXResult(true, ApiConstant.StatusCode.OK, "发送成功");
        }
        return new JXResult(false, ApiConstant.StatusCode.ERROR, "发送失败,请检查手机号");
    }
}
