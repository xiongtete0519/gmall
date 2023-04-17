package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuPosterMapper spuPosterMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    //查询一级分类
    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> baseCategory1List = baseCategory1Mapper.selectList(null);
        return baseCategory1List;
    }

    //根据一级分类id查询二级分类数据
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        //select * from base_category2 where category1_id=category1Id
        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category1_id",category1Id);

        return baseCategory2Mapper.selectList(queryWrapper);
    }

    //根据二级分类查询三级分类数据
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id",category2Id);

        return baseCategory3Mapper.selectList(queryWrapper);
    }

    /**
     * 根据分类查询平台属性
     * @param category1Id 一级分类id
     * @param category2Id 二级分类id
     * @param category3Id 三级分类id
     * @return
     */
    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
       return baseAttrInfoMapper.selectAttrInfoList(category1Id,category2Id,category3Id);
    }

    /**
     * @Transactional:
     *      使用默认配置的方式，只能对运行时异常进行回滚 RuntimeException
     *
     *      rollbackFor = Exception.class
     *      IOException
     *      SQLException
     * @param baseAttrInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //判断当前操作是保存还是修改
        if(baseAttrInfo.getId()!=null){
            //修改平台属性
            baseAttrInfoMapper.updateById(baseAttrInfo);
        }else{
            //保存平台属性
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        //根据平台属性删除属性值集合(这里是逻辑删除)
        //创建删除条件
        LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseAttrValue::getAttrId,baseAttrInfo.getId());
        baseAttrValueMapper.delete(wrapper);

        //操作平台属性值
        //新增，获取平台属性值集合
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        //判断
        if(!CollectionUtils.isEmpty(attrValueList)){
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //设置平台属性id
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                //保存
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }


    }

    //根据属性id查询属性对象
    @Override
    public BaseAttrInfo getAttrInfo(Long attrId) {
        //获取属性对象
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        //获取属性值集合
        List<BaseAttrValue> list = getAttrValueList(attrId);
        //设置属性值集合
        baseAttrInfo.setAttrValueList(list);
        return baseAttrInfo;
    }

    //根据三级分类分页查询spu列表
    @Override
    public IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> infoPage, SpuInfo spuInfo) {
        //创建条件对象
        LambdaQueryWrapper<SpuInfo> wrapper = new LambdaQueryWrapper<>();
        //设置条件
        wrapper.eq(SpuInfo::getCategory3Id,spuInfo.getCategory3Id());

        return spuInfoMapper.selectPage(infoPage,wrapper);
    }

    //获取销售属性
    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    /**
     * 保存SPU，添加事务
     * spuInfo涉及到的表
     * spu_info 基本信息表
     * spu_image    图片表
     * spu_poster   海报表
     * spu_sale_attr 销售属性表
     * spu_sale_attr_value 销售属性值表
     * @param spuInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuInfo spuInfo) {
         //保存spu信息
        spuInfoMapper.insert(spuInfo);
        //保存图片
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        //判断
        if(!CollectionUtils.isEmpty(spuImageList)){
            for (SpuImage spuImage : spuImageList) {
                //设置spuId
                spuImage.setSpuId(spuInfo.getId());
                //保存图片到数据库
                spuImageMapper.insert(spuImage);
            }
        }
        //保存海报
        List<SpuPoster> spuPosterList = spuInfo.getSpuPosterList();
        if(!CollectionUtils.isEmpty(spuPosterList)){
            for (SpuPoster spuPoster : spuPosterList) {
                //设置spuId
                spuPoster.setSpuId(spuInfo.getId());
                //保存
                spuPosterMapper.insert(spuPoster);
            }
        }
        //保存销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(!CollectionUtils.isEmpty(spuSaleAttrList)){
            //保存销售属性
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                //设置spuId
                spuSaleAttr.setSpuId(spuInfo.getId());
                //保存
                spuSaleAttrMapper.insert(spuSaleAttr);
                //获取销售属性值集合
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(!CollectionUtils.isEmpty(spuSaleAttrValueList)){
                    //保存销售属性值
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        //设置spuId
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        //设置销售属性名称
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        //保存销售属性值
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }

    }

    //根据spuId查询销售属性和销售属性值集合
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList=spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
        return spuSaleAttrList;

    }

    //根据spuId查询图片列表
    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        //创建条件对象
        LambdaQueryWrapper<SpuImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpuImage::getSpuId,spuId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(wrapper);
        return spuImageList;
    }

    /**
     * 保存skuInfo
     * skuInfo sku 基本信息表
     * sku_image sku 图片表
     * sku_sale_attr_value sku 销售属性表
     * sku_attr_value sku 平台属性表
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

        //设置is_sale
        skuInfo.setIsSale(0);
        //保存skuInfo
        skuInfoMapper.insert(skuInfo);
        //保存图片
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        //判断
        if(!CollectionUtils.isEmpty(skuImageList)){
            for (SkuImage skuImage : skuImageList) {
                //设置skuId
                skuImage.setSkuId(skuInfo.getId());
                //保存
                skuImageMapper.insert(skuImage);
            }
        }
        //保存平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        //判断
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                //设置skuId
                skuAttrValue.setSkuId(skuInfo.getId());
                //保存
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
        //保存销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(!CollectionUtils.isEmpty(skuSaleAttrValueList)){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                //设置skuId
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                //设置spuId
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                //保存
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
    }

    //sku分页列表查询
    @Override
    public IPage<SkuInfo> skuListPage(Page<SkuInfo> skuInfoPage) {
        //排序
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderBy(true,false,SkuInfo::getId);
        return skuInfoMapper.selectPage(skuInfoPage,wrapper);
    }

    //商品的上架:将is_sale改为1
    //skuInfo
    @Override
    public void onSale(Long skuId) {
        //封装对象
        SkuInfo skuInfo = new SkuInfo();
        //设置条件
        skuInfo.setId(skuId);
        //设置修改的内容
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    //商品的下架：将is_sale改为0
    @Override
    public void cancelSale(Long skuId) {
        //封装对象
        SkuInfo skuInfo = new SkuInfo();
        //设置条件
        skuInfo.setId(skuId);
        //设置修改的内容
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }

    //根据skuId查询skuInfo信息和图片列表
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        //查询skuInfo
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //根据skuId查询图片列表
        LambdaQueryWrapper<SkuImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuImage::getSkuId,skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(wrapper);
        //设置当前图片列表
        skuInfo.setSkuImageList(skuImages);
        return skuInfo;
    }

    //根据三级分类id获取分类信息(直接查视图)
    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    //根据skuId查询sku实时价格
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo!=null){
            return skuInfo.getPrice();
        }
        return BigDecimal.ZERO;
    }

    //根据skuId,spuId获取销售属性数据
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList=spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId,spuId);
        return spuSaleAttrList;
    }

    //根据spuId获取销售属性id和skuId的对应关系
    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        //查询对应关系集合
        List<Map> mapList = skuSaleAttrValueMapper.selectSkuValueIdsMap(spuId);

        //创建map整合返回的数据
        Map<Object,Object> resultMap=new HashMap<>();
        if(!CollectionUtils.isEmpty(mapList)){
            for (Map map : mapList) {
                //每个map只有一条数据
                //整合所有 key:属性id拼接的结果,value:skuId
                resultMap.put(map.get("value_ids"),map.get("sku_id"));
            }
        }

        return resultMap;
    }

    //根据属性id查询属性值集合
    private List<BaseAttrValue> getAttrValueList(Long attrId) {
        LambdaQueryWrapper<BaseAttrValue> wrapper =
                new LambdaQueryWrapper<BaseAttrValue>()
                .eq(BaseAttrValue::getAttrId, attrId);
        //查询数据
        List<BaseAttrValue> list = baseAttrValueMapper.selectList(wrapper);
        return list;
    }
}
