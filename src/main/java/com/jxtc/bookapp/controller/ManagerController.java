package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Manager;
import com.jxtc.bookapp.service.ManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "管理后台管理员接口", value = "管理后台管理员接口")
@RestController
@RequestMapping("jxapp/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;


    @ApiOperation(value = "管理员注册", notes = "管理员注册", httpMethod = "POST")
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public JXResult register(@ApiParam(value = "管理员对象", required = true)
                             @RequestBody Manager manager) {
        managerService.register(manager);
        return new JXResult(true, ApiConstant.StatusCode.OK, "注册成功");
    }

    @ApiOperation(value = "管理员登录", notes = "管理员登录", httpMethod = "POST")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public JXResult login(@ApiParam(value = "用户名", required = false)
                          @RequestParam(value = "username", defaultValue = "", required = false) String username,
                          @ApiParam(value = "密码", required = false)
                          @RequestParam(value = "password", defaultValue = "", required = false) String password) {
        Manager manager = managerService.login(username, password);
        if (manager != null) {
            return new JXResult(true, ApiConstant.StatusCode.OK, "登录成功", manager);
        }
        return new JXResult(false, ApiConstant.StatusCode.LOGINERROR, "登录失败");
    }

    @ApiOperation(value = "判断用户名是否存在", notes = "判断用户名是否存在", httpMethod = "GET")
    @RequestMapping(value = "isExistUser", method = RequestMethod.GET)
    public JXResult login(@ApiParam(value = "用户名", required = false)
                          @RequestParam(value = "username", defaultValue = "", required = false) String username) {
        boolean flag = managerService.isExistUser(username);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", flag);
    }

    @ApiOperation(value = "获得管理员列表", notes = "获得管理员列表", httpMethod = "GET")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JXResult list() {
        List<Manager> list = managerService.getList();
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", list);
    }

    @ApiOperation(value = "根据id删除管理员", notes = "根据id删除管理员", httpMethod = "DELETE")
    @RequestMapping(value = "del", method = RequestMethod.DELETE)
    public JXResult delById(@ApiParam(value = "id", required = false)
                            @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        managerService.delManagerById(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }
}
