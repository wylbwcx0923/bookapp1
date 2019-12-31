package com.jxtc.bookapp.service;

import com.jxtc.bookapp.entity.Product;

import java.util.List;

public interface ProductService {
    /**
     * 获得充值产品列表
     *
     * @return
     */
    List<Product> getProductList(String isComplex);
}
