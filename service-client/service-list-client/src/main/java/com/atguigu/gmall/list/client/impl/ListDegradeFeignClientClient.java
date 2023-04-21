package com.atguigu.gmall.list.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import org.springframework.stereotype.Component;

@Component
public class ListDegradeFeignClientClient implements ListFeignClient {
    @Override
    public Result incrHotScore(Long skuId) {
        return Result.fail();
    }
}
