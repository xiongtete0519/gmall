package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/item")
public class ItemApiController {
    @Autowired
    private ItemService itemService;

    //api/item/{skuId}
    @ApiOperation("获取商品详情数据")
    @GetMapping("{skuId}")
    public Result getItem(@PathVariable Long skuId){
        HashMap<String,Object> resultMap=itemService.getItem(skuId);
        return Result.ok(resultMap);
    }
}
