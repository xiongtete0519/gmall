package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ManagerService {

    //查询一级分类
    List<BaseCategory1> getCategory1();

    //根据一级分类id查询二级分类数据
    List<BaseCategory2> getCategory2(Long category1Id);

    //根据二级分类查询三级分类数据
    List<BaseCategory3> getCategory3(Long category2Id);

    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);

    //新增和修改平台属性
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    //根据属性id查询属性对象
    BaseAttrInfo getAttrInfo(Long attrId);

    //根据三级分类分页查询spu列表
    IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> infoPage, SpuInfo spuInfo);

    //获取销售属性
    List<BaseSaleAttr> baseSaleAttrList();

    //保存SPU
    void saveSpuInfo(SpuInfo spuInfo);

    //根据spuid查询销售属性和销售属性值集合
    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    //根据spuId查询图片列表
    List<SpuImage> spuImageList(Long spuId);

    //保存skuInfo
    void saveSkuInfo(SkuInfo skuInfo);

    //sku分页列表查询
    IPage<SkuInfo> skuListPage(Page<SkuInfo> skuInfoPage);

    //商品的上架
    void onSale(Long skuId);

    //商品的下架
    void cancelSale(Long skuId);

    //根据skuId查询skuInfo信息和图片列表
    SkuInfo getSkuInfo(Long skuId);

    //根据三级分类id获取分类信息
    BaseCategoryView getCategoryView(Long category3Id);

    //根据skuId查询sku实时价格
    BigDecimal getSkuPrice(Long skuId);

    //根据skuId,spuId获取销售属性数据
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    //根据spuId获取销售属性id和skuId的对应关系
    Map getSkuValueIdsMap(Long spuId);
}
