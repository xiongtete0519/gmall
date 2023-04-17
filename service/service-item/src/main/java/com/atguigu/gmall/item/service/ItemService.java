package com.atguigu.gmall.item.service;

import java.util.HashMap;

public interface ItemService {

    //获取商品详情数据
    HashMap<String, Object> getItem(Long skuId);
}
