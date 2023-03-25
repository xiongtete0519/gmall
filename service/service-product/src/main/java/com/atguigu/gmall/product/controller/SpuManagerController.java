package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "SPU")
@RestController
@RequestMapping("/admin/product")
public class SpuManagerController {

    @Autowired
    private ManagerService managerService;

    @ApiOperation("根据三级分类分页查询spu列表")
    @GetMapping("{page}/{limit}")
    public Result getSpuInfoPage(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 SpuInfo spuInfo){
        //封装参数
        Page<SpuInfo> infoPage=new Page<>(page,limit);

        //根据三级分类id查询Spu列表
        IPage<SpuInfo> infoIPage=managerService.getSpuInfoPage(infoPage,spuInfo);

        return Result.ok(infoIPage);
    }

    @ApiOperation("获取销售属性")
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList=managerService.baseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }


}
