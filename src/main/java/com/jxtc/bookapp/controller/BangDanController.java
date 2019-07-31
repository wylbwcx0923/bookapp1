package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Bangdan;
import com.jxtc.bookapp.entity.BangdanBooks;
import com.jxtc.bookapp.service.BangDanService;
import com.jxtc.bookapp.utils.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 不忘初心
 */
@Api(value = "榜单接口", tags = "榜单接口")
@RequestMapping("/jxapp/bangdan")
@Controller
public class BangDanController {

    @Autowired
    private BangDanService bangDanService;

    @ApiOperation(value = "新建榜单接口", notes = "新建榜单接口", httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public JXResult addBangdan(@ApiParam(value = "榜单", required = false)
                               @RequestBody Bangdan bangdan) {
        bangDanService.insertBangDan(bangdan);
        return new JXResult(true, ApiConstant.StatusCode.OK, "添加成功");
    }

    @ApiOperation(value = "删除榜单接口", notes = "删除榜单接口", httpMethod = "DELETE")
    @ResponseBody
    @RequestMapping(value = "del", method = RequestMethod.DELETE)
    public JXResult deleteBangdan(@ApiParam(value = "id", required = false)
                                  @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        bangDanService.deleteBangDan(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }

    @ApiOperation(value = "修改榜单接口", notes = "修改榜单接口", httpMethod = "PUT")
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public JXResult updateBangdan(@ApiParam(value = "榜单", required = false)
                                  @RequestBody Bangdan bangdan) {
        bangDanService.updateBangDan(bangdan);
        return new JXResult(true, ApiConstant.StatusCode.OK, "修改成功");
    }

    @ApiOperation(value = "通过id查询榜单接口", notes = "通过id查询榜单接口", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "findbyid", method = RequestMethod.GET)
    public JXResult findBangdanById(@ApiParam(value = "id", required = false)
                                    @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        Bangdan bangdan = bangDanService.findById(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bangdan);
    }

    @ApiOperation(value = "查询榜单列表接口", notes = "查询榜单列表接口", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JXResult getBangDanList(@ApiParam(value = "当前页", required = false)
                                   @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                   @ApiParam(value = "每页显示数量", required = false)
                                   @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        PageResult<Bangdan> bangDanList = bangDanService.getBangDanList(pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bangDanList);
    }

    @ApiOperation(value = "根据榜单id获得榜单中的书", notes = "根据榜单id获得榜单中的书", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "list/books", method = RequestMethod.GET)
    public JXResult getBangDanBookList(@ApiParam(value = "榜单Id", required = false)
                                       @RequestParam(value = "bangdanId", defaultValue = "1", required = false) int bangdanId,
                                       @ApiParam(value = "当前页", required = false)
                                       @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                                       @ApiParam(value = "每页显示数量", required = false)
                                       @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
        PageResult<BangdanBooks> pageResult = bangDanService.getBangDanBooks(bangdanId, pageIndex, pageSize);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", pageResult);
    }

    @ApiOperation(value = "向某个榜单中添加书籍", notes = "向某个榜单中添加书籍", httpMethod = "POST")
    @ResponseBody
    @RequestMapping(value = "add/books", method = RequestMethod.POST)
    public JXResult addBangDanBooks(@ApiParam(value = "榜单书籍", required = false)
                                    @RequestBody BangdanBooks bangdanBooks) {
        bangDanService.insertBangDanBooks(bangdanBooks);
        return new JXResult(true, ApiConstant.StatusCode.OK, "添加成功");
    }

    @ApiOperation(value = "删除榜单中的某本书", notes = "删除榜单中的某本书", httpMethod = "DELETE")
    @ResponseBody
    @RequestMapping(value = "del/books", method = RequestMethod.DELETE)
    public JXResult delBangDanBooks(@ApiParam(value = "id", required = false)
                                    @RequestParam(value = "id", defaultValue = "", required = false) int id) {
        bangDanService.deleteBangDanBooks(id);
        return new JXResult(true, ApiConstant.StatusCode.OK, "删除成功");
    }

    @ApiOperation(value = "根据分类类型获得推荐菜单", notes = "根据分类类型获得推荐菜单", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "get/bangdannames", method = RequestMethod.GET)
    public JXResult getBangdannames(@ApiParam(value = "type", required = false)
                                    @RequestParam(value = "type", defaultValue = "", required = false) int type) {
        List<Bangdan> bangdans = bangDanService.getBangDanListByType(type);
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", bangdans);
    }
}
