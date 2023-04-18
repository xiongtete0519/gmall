package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            resultMap.put("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));
            resultMap.put("spuPosterList",spuPosterBySpuId);
        }
        //获取平台信息
        List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
        //处理数据符合要求 List  Obj  key attrName value attrValue
        List<Map<String, String>> spuAttrList = attrList.stream().map(baseAttrInfo -> {
            Map<String, String> map = new HashMap<>();
            map.put("attrName", baseAttrInfo.getAttrName());
            map.put("attrValue", baseAttrInfo.getAttrValueList().get(0).getValueName());
            return map;
        }).collect(Collectors.toList());

        //存储数据
        resultMap.put("skuInfo",skuInfo);
        resultMap.put("price",skuPrice);
        resultMap.put("skuAttrList",spuAttrList);
        return resultMap;
    }
}
