package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class BaseTrademarkServiceImpl implements BaseTrademarkService {

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    //品牌分页列表查询
    @Override
    public IPage<BaseTrademark> getBaseTrademarkPage(Page<BaseTrademark> baseTrademarkPage) {
        //排序
        LambdaQueryWrapper<BaseTrademark> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BaseTrademark::getId);
        return baseTrademarkMapper.selectPage(baseTrademarkPage,null);
    }
}
