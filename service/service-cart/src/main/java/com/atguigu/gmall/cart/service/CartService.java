package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

public interface CartService {
    //加入购物车
    void addToCart(Long skuId, Integer skuNum, String userId);

    //展示购物车
    List<CartInfo> cartList(String userId, String userTempId);
}
