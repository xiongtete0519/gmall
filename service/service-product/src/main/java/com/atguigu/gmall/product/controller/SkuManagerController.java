package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "sku")
@RestController
@RequestMapping("/admin/product")
public class SkuManagerController {

    @Autowired
    private ManagerService managerService;

    @ApiOperation("sku分页列表查询")
    @GetMapping("/list/{page}/{limit}")
    public Result skuListPage(@PathVariable Long page, @PathVariable Long limit){
        //封装分页对象
        Page<SkuInfo> skuInfoPage = new Page<>(page, limit);
        IPage<SkuInfo> infoIPage=managerService.skuListPage(skuInfoPage);
        return Result.ok(infoIPage);
    }

    @ApiOperation("保存skuInfo")
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        managerService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @ApiOperation("根据spuId查询图片列表")
    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable Long spuId){
        List<SpuImage> spuImageList=managerService.spuImageList(spuId);
        return Result.ok(spuImageList);
    }

    @ApiOperation("根据spuId查询销售属性和销售属性值集合")
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){

        List<SpuSaleAttr> spuSaleAttrList=managerService.spuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }
}
