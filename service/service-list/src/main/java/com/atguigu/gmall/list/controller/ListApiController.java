package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/list")
public class ListApiController {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private SearchService searchService;

    @ApiOperation("商品上架")
    @GetMapping("/inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable Long skuId){

        searchService.upperGoods(skuId);
        return Result.ok();
    }

    @ApiOperation("创建索引库，建立mapping结构")
    @GetMapping("createIndex")
    public Result createIndex(){
        //创建索引库
        restTemplate.createIndex(Goods.class);
        //建立mapping
        restTemplate.putMapping(Goods.class);

        return Result.ok();
    }


}
