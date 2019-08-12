package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Reward;
import com.jxtc.bookapp.entity.RewardRank;
import com.jxtc.bookapp.service.RewardService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "用户打赏接口", tags = "用户打赏接口")
@RequestMapping("/jxapp/reward")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @ApiOperation(value = "用户打赏", notes = "用户打赏", httpMethod = "POST")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public JXResult addReward(@ApiParam(value = "打赏对象", required = true)
                              @RequestBody Reward reward,
                              @ApiParam(value = "优惠券Id", required = false)
                              @RequestParam(value = "couponId", required = false) Integer couponId) {
        System.out.println(reward);
        int flog = rewardService.insert(reward,couponId);
        if (flog == 0) {
            return new JXResult(false, ApiConstant.StatusCode.OK, "打赏失败");
        } else {
            return new JXResult(true, ApiConstant.StatusCode.OK, "打赏成功");
        }
    }

    @ApiOperation(value = "获取打赏排名", notes = "获取打赏排名", httpMethod = "GET")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JXResult listReward(@ApiParam(value = "图书id", required = false)
                               @RequestParam(value = "bookId", defaultValue = "", required = false) int bookId,
                               @ApiParam(value = "当前页", required = false)
                               @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                               @ApiParam(value = "每页显示数量", required = false)
                               @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        PageResult<RewardRank> page = rewardService.getPageRewards(bookId, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", page);
    }

}
