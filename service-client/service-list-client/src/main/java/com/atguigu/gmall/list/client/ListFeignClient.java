package com.atguigu.gmall.list.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.impl.ListDegradeFeignClientClient;
import com.atguigu.gmall.model.list.SearchParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-list",fallback = ListDegradeFeignClientClient.class)
public interface ListFeignClient {

    @ApiOperation("商品搜索")
    @PostMapping("/api/list")
    public Result list(@RequestBody SearchParam searchParam);

    @ApiOperation("更新商品的热度排名")
    @GetMapping("/api/list/inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable Long skuId);
}
