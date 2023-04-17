package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.ManagerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product/inner")
public class ProductApiController {
    @Autowired
    private ManagerService managerService;

    @ApiOperation("根据skuId查询skuInfo信息和图片列表")
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId){
        SkuInfo skuInfo=managerService.getSkuInfo(skuId);
        return skuInfo;
    }
}
