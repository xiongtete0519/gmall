package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategoryTrademark;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import com.atguigu.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
public class BaseCategoryTrademarkServiceImpl extends ServiceImpl<BaseCategoryTrademarkMapper,BaseCategoryTrademark> implements BaseCategoryTrademarkService {

    //品牌表
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    //分类品牌中间表
    @Autowired
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;

    //根据category3Id获取品牌列表(多对多查询)
    @Override
    public List<BaseTrademark> findTrademarkList(Long category3Id) {

        List<BaseTrademark> baseTrademarkList=new ArrayList<>();
        //查询条件
        LambdaQueryWrapper<BaseCategoryTrademark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategoryTrademark::getCategory3Id,category3Id);

        //根据三级分类id查询中间表获取关联的品牌id
        List<BaseCategoryTrademark> baseCategoryTrademarkList = baseCategoryTrademarkMapper.selectList(wrapper);

        //处理获取品牌id
        if(!CollectionUtils.isEmpty(baseCategoryTrademarkList)){
            //stream流 filter:过滤 map:获取遍历过程中指定的数据
            List<Long> trademarkIdList = baseCategoryTrademarkList
                    .stream()
                    .map(x -> x.getTrademarkId())
                    .collect(Collectors.toList());

            //根据品牌id集合，查询品牌集合数据
//            for (Long id : trademarkIdList) {
//                BaseTrademark baseTrademark = baseTrademarkMapper.selectById(id);
//                baseTrademarkList.add(baseTrademark);
//            }
            LambdaQueryWrapper<BaseTrademark> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.in(BaseTrademark::getId,trademarkIdList);
            baseTrademarkList = baseTrademarkMapper.selectList(wrapper1);
        }
        return baseTrademarkList;
    }

    //删除分类品牌关联
    @Override
    public void remove(Long category3Id, Long trademarkId) {

//        LambdaQueryChainWrapper<BaseCategoryTrademark> wrapper = new LambdaQueryChainWrapper<>(baseCategoryTrademarkMapper);
        LambdaQueryWrapper<BaseCategoryTrademark> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(BaseCategoryTrademark::getCategory3Id,category3Id)
                .eq(BaseCategoryTrademark::getTrademarkId,trademarkId);
        //删除关联
        baseCategoryTrademarkMapper.delete(wrapper);
    }

    //根据category3Id获取可选品牌列表
    @Override
    public List<BaseTrademark> findCurrentTrademarkList(Long category3Id) {
        List<BaseTrademark> result=new ArrayList<>();

        LambdaQueryWrapper<BaseCategoryTrademark> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(BaseCategoryTrademark::getCategory3Id,category3Id);
        List<BaseCategoryTrademark> baseCategoryTrademarkList = baseCategoryTrademarkMapper.selectList(wrapper);
        if(!CollectionUtils.isEmpty(baseCategoryTrademarkList)){
            //获取当前分类已经关联的品牌id
            List<Long> tradeMarkIdList= baseCategoryTrademarkList.stream()
                    .map(x -> x.getTrademarkId())
                    .collect(Collectors.toList());
//            //查询所有品牌
//            List<BaseTrademark> baseTrademarkList = baseTrademarkMapper.selectList(null);
//            //遍历处理
//            result = baseTrademarkList.stream().filter(baseTrademark -> {
//                return !tradeMarkIdList.contains(baseTrademark.getId());
//            }).collect(Collectors.toList());
            //创建条件对象
            LambdaQueryWrapper<BaseTrademark> wrapper1 = Wrappers.lambdaQuery();
            wrapper1.notIn(BaseTrademark::getId,tradeMarkIdList);
            //条件查询品牌列表
            result = baseTrademarkMapper.selectList(wrapper1);
        }
        return result;
    }

    //保存分类和品牌关联
    @Override
    public void save(CategoryTrademarkVo categoryTrademarkVo) {

        //获取品牌id集合
        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();
        //遍历处理，封装成对象
        List<BaseCategoryTrademark> baseCategoryTrademarkList = trademarkIdList.stream().map(trademarkId -> {
            //创建分类品牌对象
            BaseCategoryTrademark baseCategoryTrademark = new BaseCategoryTrademark();
            baseCategoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
            baseCategoryTrademark.setTrademarkId(trademarkId);
            return baseCategoryTrademark;
        }).collect(Collectors.toList());
        this.saveBatch(baseCategoryTrademarkList);
    }
}
