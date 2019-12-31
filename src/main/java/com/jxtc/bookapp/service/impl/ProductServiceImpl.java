package com.jxtc.bookapp.service.impl;

import com.jxtc.bookapp.entity.Product;
import com.jxtc.bookapp.entity.ProductExample;
import com.jxtc.bookapp.mapper.app.ProductMapper;
import com.jxtc.bookapp.service.ProductService;
import com.jxtc.bookapp.service.RedisService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public List<Product> getProductList(String isComplex) {
        //从缓存中取
        String isExists = (String) redisService.get("product_list_" + isComplex);
        List<Product> products = new ArrayList<>();
        if (isExists == null || "".equals(isExists) || isExists.length() < 5) {
            //取不到从mysql取
            ProductExample example = null;
            if (StringUtils.isNotBlank(isComplex) && isComplex.equals("1")) {
                example = new ProductExample();
                example.createCriteria().andProductInfoEqualTo("2");

            } else {
                example = new ProductExample();
                example.createCriteria().andProductInfoEqualTo("1");
            }
            products = productMapper.selectByExample(example);
            if (products != null && products.size() > 0) {
                String arystr = JSONArray.fromObject(products).toString();
                logger.info("获得的充值产品的列表为:" + arystr);
                redisService.set("product_list_" + isComplex, arystr);
            }
        } else {
            //缓存中有,从缓存中取
            JSONArray array = JSONArray.fromObject(isExists);
            products = (List<Product>) JSONArray.toCollection(array, Product.class);
        }
        return products;
    }

}
