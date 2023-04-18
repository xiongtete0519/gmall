package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManagerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product/inner")
public class ProductApiController {
    @Autowired
    private ManagerService managerService;

    @ApiOperation("根据skuId查询平台属性和平台属性值")
    @GetMapping("/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable Long skuId){
        return managerService.getAttrList(skuId);
    }

    @ApiOperation("根据spuId查询海报集合数据")
    @GetMapping("/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable Long spuId){
        return managerService.findSpuPosterBySpuId(spuId);
    }

    @ApiOperation("根据spuId获取销售属性id和skuId的对应关系")
    @GetMapping("/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId){

        return managerService.getSkuValueIdsMap(spuId);
    }

    @ApiOperation("根据skuId,spuId获取销售属性数据")
    //getSpuSaleAttrListCheckBySku/{skuId}/{spuId}
    @GetMapping("/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId,
                                             @PathVariable Long spuId){
        return managerService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }

    @ApiOperation("根据skuId查询sku实时价格")
    @GetMapping("getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId){
        return managerService.getSkuPrice(skuId);
    }

    @ApiOperation("根据skuId查询skuInfo信息和图片列表")
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId){
        SkuInfo skuInfo=managerService.getSkuInfo(skuId);
        return skuInfo;
    }

    @ApiOperation("根据三级分类id获取分类信息")
    @GetMapping("/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id){

        return managerService.getCategoryView(category3Id);
    }
}
