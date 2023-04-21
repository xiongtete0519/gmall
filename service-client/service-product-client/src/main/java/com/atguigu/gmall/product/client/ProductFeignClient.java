package com.atguigu.gmall.product.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.impl.ProductDegradeFeignClient;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * value:服务名称
 */
@FeignClient(value = "service-product",fallback = ProductDegradeFeignClient.class)
public interface ProductFeignClient {

    @ApiOperation("根据品牌id获取品牌数据")
    @GetMapping("/api/product/inner/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable Long tmId);

    @ApiOperation("首页数据查询三级分类数据")
    @GetMapping("/api/product/inner/getBaseCategoryList")
    public Result getBaseCategoryList();

    @ApiOperation("根据skuId查询平台属性和平台属性值")
    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable Long skuId);

    @ApiOperation("根据spuId查询海报集合数据")
    @GetMapping("/api/product/inner/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable Long spuId);

    @ApiOperation("根据spuId获取销售属性id和skuId的对应关系")
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId);

    @ApiOperation("根据skuId,spuId获取销售属性数据")
    //getSpuSaleAttrListCheckBySku/{skuId}/{spuId}
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId, @PathVariable Long spuId);

    @ApiOperation("根据skuId查询sku实时价格")
    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId);

    @ApiOperation("根据skuId查询skuInfo信息和图片列表")
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId);

    @ApiOperation("根据三级分类id获取分类信息")
    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id);
}
