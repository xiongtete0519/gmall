package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/list")
public class ListApiController {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private SearchService searchService;

    @ApiOperation("商品搜索")
    @PostMapping
    public Result list(@RequestBody SearchParam searchParam){
        SearchResponseVo searchResponseVo=searchService.search(searchParam);
        return Result.ok(searchResponseVo);
    }

    @ApiOperation("更新商品的热度排名")
    @GetMapping("/inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable Long skuId){
        searchService.incrHotScore(skuId);
        return Result.ok();
    }

    @ApiOperation("商品下架")
    @GetMapping("inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable Long skuId){
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

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
