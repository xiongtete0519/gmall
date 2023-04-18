package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;

    //获取商品详情数据
    @Override
    public HashMap<String, Object> getItem(Long skuId) {
        HashMap<String, Object> resultMap=new HashMap<>();
        //获取sku的基本详情和图片列表
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        //获取实时价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);

        //判断
        if(skuInfo!=null){
            //获取三级分类
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            //获取销售属性和选中状态
            List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            //获取商品切换数据
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            //获取海报信息
            List<SpuPoster> spuPosterBySpuId = productFeignClient.findSpuPosterBySpuId(skuInfo.getSpuId());

            resultMap.put("categoryView",categoryView);
            resultMap.put("spuSaleAttrList",spuSaleAttrListCheckBySku);
            resultMap.put("valueSkuJson",skuValueIdsMap);
            resultMap.put("spuPosterList",spuPosterBySpuId);
        }
        //获取平台信息
        List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);

        //存储数据
        resultMap.put("skuInfo",skuInfo);
        resultMap.put("price",skuPrice);
        resultMap.put("skuAttrList",attrList);
        return resultMap;
    }
}
