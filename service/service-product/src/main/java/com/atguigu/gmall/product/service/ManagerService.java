package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface ManagerService {

    //查询一级分类
    List<BaseCategory1> getCategory1();

    //根据一级分类id查询二级分类数据
    List<BaseCategory2> getCategory2(Long category1Id);

    //根据二级分类查询三级分类数据
    List<BaseCategory3> getCategory3(Long category2Id);

    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);

    //新增和修改平台属性
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    //根据属性id查询属性对象
    BaseAttrInfo getAttrInfo(Long attrId);

    //根据三级分类分页查询spu列表
    IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> infoPage, SpuInfo spuInfo);
}
