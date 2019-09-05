package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Canal;
import com.jxtc.bookapp.entity.CanalPopularizeCount;
import com.jxtc.bookapp.entity.Equipment;
import com.jxtc.bookapp.service.CanalPopularizeService;
import com.jxtc.bookapp.service.EquipmentService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Api(value = "渠道推广接口", tags = "渠道推广接口")
@RequestMapping(value = "/jxapp/canal")
public class CanalPopularizeController {

    @Autowired
    private CanalPopularizeService canalPopularizeService;
    @Autowired
    private EquipmentService equipmentService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiOperation(value = "添加渠道", notes = "添加渠道", httpMethod = "POST")
    public JXResult addCanal(@ApiParam(value = "渠道对象", defaultValue = "", required = true)
                             @RequestBody Canal canal) {
        canalPopularizeService.addCanal(canal);
        return new JXResult(true, ApiConstant.StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiOperation(value = "修改渠道", notes = "修改渠道", httpMethod = "PUT")
    public JXResult updateCanal(@ApiParam(value = "渠道对象", defaultValue = "", required = true)
                                @RequestBody Canal canal) {
        canalPopularizeService.updateCanal(canal);
        return new JXResult(true, ApiConstant.StatusCode.OK, "修改成功");
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ApiOperation(value = "获得渠道列表", notes = "获得渠道列表", httpMethod = "GET")
    public JXResult getCanalList() {
        List<Canal> canalList = canalPopularizeService.getCanalList();
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", canalList);
    }

    @RequestMapping(value = "del", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除某个渠道", notes = "删除某个渠道", httpMethod = "DELETE")
    public JXResult delCanalById(@ApiParam(value = "id", defaultValue = "", required = false)
                                 @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        canalPopularizeService.deleteCanal(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }

    @RequestMapping(value = "popularize/list", method = RequestMethod.GET)
    @ApiOperation(value = "获得渠道推广列表数据", notes = "获得渠道推广列表数据", httpMethod = "GET")
    public JXResult getCanalPopularizeCountList(@ApiParam(value = "当前页", required = false)
                                                @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                                @ApiParam(value = "每页显示数量", required = false)
                                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                                @ApiParam(value = "查询的起始时间(格式:yyyy-MM-dd)", required = false)
                                                @RequestParam(value = "startTime", defaultValue = "", required = false) String startTime,
                                                @ApiParam(value = "查询的结束时间(格式:yyyy-MM-dd)", required = false)
                                                @RequestParam(value = "endTime", defaultValue = "", required = false) String endTime,
                                                @ApiParam(value = "渠道ID", required = false)
                                                @RequestParam(value = "canalId", defaultValue = "", required = false) Integer canalId) {
        PageResult<CanalPopularizeCount> page = canalPopularizeService.getCanalPopularizeCountList(pageIndex, pageSize, canalId, startTime, endTime);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }

    @RequestMapping(value = "set/remark/cost", method = RequestMethod.PUT)
    @ApiOperation(value = "设置备注或成本", notes = "设置备注或成本", httpMethod = "PUT")
    public JXResult updateCanal(@ApiParam(value = "id", defaultValue = "", required = true)
                                @RequestParam(value = "id", defaultValue = "", required = true) int id,
                                @ApiParam(value = "备注", defaultValue = "", required = false)
                                @RequestParam(value = "remark", defaultValue = "", required = false) String remark,
                                @ApiParam(value = "成本", defaultValue = "", required = false)
                                @RequestParam(value = "cose", defaultValue = "", required = false) Double cose) {
        canalPopularizeService.setCanalPopularizeCountRemarkOrCose(remark, id, cose);
        return new JXResult(true, ApiConstant.StatusCode.OK, "设置成功");
    }

    @RequestMapping(value = "canal/user/total", method = RequestMethod.GET)
    @ApiOperation(value = "获得每个渠道的下载总人数", notes = "获得每个渠道的下载总人数", httpMethod = "GET")
    public JXResult getCanalUserTotal() {
        Map<String, Object> map = canalPopularizeService.getDifferenceCanalUserCounts();
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", map);
    }

    @RequestMapping(value = "equipment/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户的手机编号", notes = "添加用户的手机编号", httpMethod = "POST")
    public JXResult addEquipment(@ApiParam(value = "手机编号对象", required = true, defaultValue = "")
                                 @RequestBody Equipment equipment) {
        equipmentService.addEquipment(equipment);
        return new JXResult(true, ApiConstant.StatusCode.OK, "添加成功");
    }
}
