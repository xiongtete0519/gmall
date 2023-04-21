package com.atguigu.gmall.list.service;

public interface SearchService {
    //商品上架
    void upperGoods(Long skuId);

    //商品下架
    void lowerGoods(Long skuId);

    //更新商品的热度排名
    void incrHotScore(Long skuId);
}
