package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface BaseTrademarkService {

    //品牌分页列表查询
    IPage<BaseTrademark> getBaseTrademarkPage(Page<BaseTrademark> baseTrademarkPage);
}
