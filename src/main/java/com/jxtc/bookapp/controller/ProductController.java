package com.jxtc.bookapp.controller;

import com.jxtc.bookapp.config.ApiConstant;
import com.jxtc.bookapp.config.JXResult;
import com.jxtc.bookapp.entity.Product;
import com.jxtc.bookapp.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "充值产品接口", value = "充值产品接口")
@RequestMapping("jxapp/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation(value = "获得普通充值的产品列表", notes = "获得普通充值的产品列表", httpMethod = "GET")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JXResult getList() {
        List<Product> productList = productService.getProductList();
        return new JXResult(true, ApiConstant.StatusCode.OK, "请求成功", productList);
    }

}
