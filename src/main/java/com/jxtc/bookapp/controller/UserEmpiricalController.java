package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.service.UserEmpiricalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(value = "用户经验值和任务接口", tags = "用户经验值和任务接口")
@RestController
@RequestMapping("jxapp/empirical")
public class UserEmpiricalController {

    @Autowired
    private UserEmpiricalService userEmpiricalService;

    @ApiOperation(value = "做任务接口", notes = "做任务接口", httpMethod = "PUT")
    @RequestMapping(value = "dotask", method = RequestMethod.PUT)
    public JXResult doTask(@ApiParam(value = "用户Id", required = false)
                           @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
                           @ApiParam(value = "任务类型(1.签到,2.分享,3.阅读,4.阅读指定书,5.绑定微信,6.绑定QQ)", required = false)
                           @RequestParam(value = "taskType", defaultValue = "", required = false) int taskType) {
        userEmpiricalService.doTask(userId, taskType);
        return new JXResult(true, ApiConstant.StatusCode.OK, "完成任务成功");
    }

    @ApiOperation(value = "查看任务的状态", notes = "查看任务的状态", httpMethod = "GET")
    @RequestMapping(value = "checktask", method = RequestMethod.GET)
    public JXResult checkTask(@ApiParam(value = "用户Id", required = false)
                              @RequestParam(value = "userId", defaultValue = "", required = false) String userId) {
        Map<String, Boolean> map = userEmpiricalService.checkTaskStatus(userId);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", map);
    }
}
