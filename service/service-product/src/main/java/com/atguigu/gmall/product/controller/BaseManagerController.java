package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "商品基础开发接口")
//@CrossOrigin
@RestController
@RequestMapping("/admin/product")
public class BaseManagerController {
    @Autowired
    private ManagerService managerService;

    @ApiOperation("根据属性id查询属性值集合")
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){
        BaseAttrInfo baseAttrInfo=managerService.getAttrInfo(attrId);
        List<BaseAttrValue> list=baseAttrInfo.getAttrValueList();
        return Result.ok(list);
    }

    @ApiOperation("新增和修改平台属性")
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        managerService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    @ApiOperation("根据分类查询平台属性")
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id,
                               @PathVariable Long category2Id,
                               @PathVariable Long category3Id){
        List<BaseAttrInfo> list = managerService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(list);
    }

    //查询一级分类
    @ApiOperation("查询一级分类")
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        //查询数据
        List<BaseCategory1> baseCategory1List=managerService.getCategory1();

        return Result.ok(baseCategory1List);
    }

    @ApiOperation("根据一级分类id查询二级分类数据")
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id){
        List<BaseCategory2> category2List=managerService.getCategory2(category1Id);
        return Result.ok(category2List);
    }

    @ApiOperation("根据二级分类查询三级分类数据")
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id){
        List<BaseCategory3> category3List=managerService.getCategory3(category2Id);
        return Result.ok(category3List);
    }
}
