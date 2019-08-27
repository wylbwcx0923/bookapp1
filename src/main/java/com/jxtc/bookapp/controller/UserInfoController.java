package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.UserInfo;
import com.jxtc.bookapp.service.UserInfoService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(value = "/jxapp/user")
@Api(value = "用户接口", tags = "用户接口")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 微信登录
     *
     * @return
     */
    @RequestMapping(value = "/wx/login", method = RequestMethod.GET)
    @ApiOperation(value = "通过微信登录", notes = "通过微信登录", httpMethod = "GET")
    @ResponseBody
    public JXResult wxLogin(@ApiParam(value = "微信授权的临时票据", required = true)
                            @RequestParam(value = "code", defaultValue = "", required = false) String code) {
        String userId = userInfoService.wxLogin(code);
        if (StringUtils.isEmpty(userId)) {
            return new JXResult(false, ApiConstant.StatusCode.LOGINERROR, "微信授权失败!请重试!");
        }
        return new JXResult(true, ApiConstant.StatusCode.OK, "微信授权成功,登录成功!", userId);
    }

    @RequestMapping(value = "/qq/login", method = RequestMethod.POST)
    @ApiOperation(value = "通过qq登录", notes = "通过qq登录", httpMethod = "POST")
    @ResponseBody
    public JXResult qqLogin(@ApiParam(value = "用户信息", required = true)
                            @RequestBody UserInfo userInfo) {
        String userId = userInfoService.qqLogin(userInfo);
        if (StringUtils.isEmpty(userId)) {
            return new JXResult(false, ApiConstant.StatusCode.LOGINERROR, "QQ授权失败!请重试!");
        }
        return new JXResult(true, ApiConstant.StatusCode.OK, "QQ授权成功,登录成功!", userId);
    }

    @RequestMapping(value = "/get/userinfo", method = RequestMethod.GET)
    @ApiOperation(value = "通过用户名获得用户信息", notes = "通过用户名获得用户信息", httpMethod = "GET")
    @ResponseBody
    public JXResult getUserInfo(@ApiParam(value = "用户Id", required = false)
                                @RequestParam(value = "userId", required = false, defaultValue = "") String userId) {
        Map<String, Object> map = userInfoService.getUserInfo(userId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", map);
    }

    @RequestMapping(value = "/update/userinfo", method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户的基本信息", notes = "修改用户的基本信息", httpMethod = "PUT")
    @ResponseBody
    public JXResult updateUserInfo(@ApiParam(value = "用户信息", required = true)
                                   @RequestBody UserInfo userInfo) {
        userInfoService.updateUser(userInfo);
        return new JXResult(true, ApiConstant.StatusCode.OK, "修改成功");
    }

    @RequestMapping(value = "/bind/weixin", method = RequestMethod.PUT)
    @ApiOperation(value = "绑定微信信息", notes = "绑定微信信息", httpMethod = "PUT")
    @ResponseBody
    public JXResult updateUserInfo(@ApiParam(value = "用户Id", required = false)
                                   @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                                   @ApiParam(value = "微信授权的临时票据", required = true)
                                   @RequestParam(value = "code", defaultValue = "", required = false) String code) {
        userInfoService.bindWeiXin(userId, code);
        return new JXResult(true, ApiConstant.StatusCode.OK, "微信修改成功");
    }

    @RequestMapping(value = "/bind/qq", method = RequestMethod.PUT)
    @ApiOperation(value = "绑定QQ信息", notes = "绑定QQ信息", httpMethod = "PUT")
    @ResponseBody
    public JXResult updateUserInfo(@ApiParam(value = "用户Id", required = false)
                                   @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                                   @ApiParam(value = "用户信息", required = true)
                                   @RequestBody UserInfo userInfo) {
        userInfoService.bindQQ(userId, userInfo);
        return new JXResult(true, ApiConstant.StatusCode.OK, "QQ绑定成功");
    }

    @RequestMapping(value = "/sms/login", method = RequestMethod.POST)
    @ApiOperation(value = "验证手机验证码(手机号登录)", notes = "验证手机验证码(手机号登录)", httpMethod = "POST")
    @ResponseBody
    public JXResult smsLogin(@ApiParam(value = "手机号", required = false)
                             @RequestParam(value = "phoneNumber", defaultValue = "", required = false) String phoneNumber,
                             @ApiParam(value = "验证码", required = true)
                             @RequestParam(value = "code", defaultValue = "", required = false) String code) {
        Map<String, Object> map = userInfoService.smsVerify(phoneNumber, code);
        int errorCode = (int) map.get("errorCode");
        if (errorCode == 200) {
            return new JXResult(true, ApiConstant.StatusCode.OK, "手机号登录成功", map);
        }
        return new JXResult(false, ApiConstant.StatusCode.LOGINERROR, "登录失败", map);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "获得用户列表", notes = "获得用户列表", httpMethod = "GET")
    @ResponseBody
    public JXResult getUserList(@ApiParam(value = "当前页", required = false)
                                @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                @ApiParam(value = "每页显示数量", required = false)
                                @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
                                @ApiParam(value = "用户Id", required = false)
                                @RequestParam(value = "userId", defaultValue = "", required = false) String userId) {
        PageResult<UserInfo> page = userInfoService.getUserList(pageIndex, pageSize, userId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }

    @RequestMapping(value = "/send/coin", method = RequestMethod.PUT)
    @ApiOperation(value = "给用户赠送阅币", notes = "给用户赠送阅币", httpMethod = "PUT")
    @ResponseBody
    public JXResult sendCoin(@ApiParam(value = "用户Id", required = false)
                             @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                             @ApiParam(value = "赠送的阅币书", required = false)
                             @RequestParam(value = "coin", defaultValue = "", required = false) int coin) {
        userInfoService.sendCoin(userId, coin);
        return new JXResult(true, ApiConstant.StatusCode.OK, "赠送成功");
    }

    @RequestMapping(value = "/verb/user", method = RequestMethod.PUT)
    @ApiOperation(value = "重置用户", notes = "重置用户", httpMethod = "PUT")
    @ResponseBody
    public JXResult verbUserByUserId(@ApiParam(value = "用户Id", required = false)
                             @RequestParam(value = "userId", defaultValue = "", required = false) String userId) {
        userInfoService.verbUser(userId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "用户重置成功");
    }
}
