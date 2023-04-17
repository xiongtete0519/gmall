package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ItemServiceImpl implements ItemService {

//    @Autowired
//    private ProductFeignClient productFeignClient;

    //获取商品详情数据
    @Override
    public HashMap<String, Object> getItem(Long skuId) {
        HashMap<String, Object> resultMap=new HashMap<>();
                //获取sku的基本详情和图片列表
//        productFeignClient
        return resultMap;
    }
}
