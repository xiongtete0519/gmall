package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@SuppressWarnings("all")
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 加入购物车
     * 存储数据：
     * 1、区别用户
     * 2、区别商品
     *
     * Hash
     *  key:用户
     *  field:商品
     *  value:商品对象信息
     *
     *  user:1:cart  22  CartInfo
     *  指令：HSET key field value
     *
     *  思路：
     *      1、先根据用户获取购物车列表
     *      2、判断是否存在当前添加的商品
     *          存在：数量相加
     *          不存在：新建添加
     */
    @Override
    public void addToCart(Long skuId, Integer skuNum, String userId) {
        //定义key
        String cartKey=this.getKey(userId);
        //获取购物车列表 String=user:1:cart ,  String=22,CartInfo
        BoundHashOperations<String,String, CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        //定义购物车详情信息对象
        CartInfo cartInfo=null;
        //判断当前列表是否包含sku
        if(boundHashOperations.hasKey(skuId.toString())){
            //存在
            cartInfo = boundHashOperations.get(skuId.toString());
            cartInfo.setSkuNum(cartInfo.getSkuNum()+skuNum);
            cartInfo.setUpdateTime(new Date());
            cartInfo.setSkuPrice(productFeignClient.getSkuPrice(skuId));

        }else{
            //不存在
            cartInfo = new CartInfo();
            cartInfo.setUserId(userId);
            cartInfo.setSkuId(skuId);
            //远程请求sku详情数据
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
            //实时价格
            cartInfo.setSkuPrice(skuInfo.getPrice());
        }
        //存储
        boundHashOperations.put(skuId.toString(),cartInfo);

    }

    //展示购物车
    @Override
    public List<CartInfo> cartList(String userId, String userTempId) {

        List<CartInfo> cartInfoList=null;


        //判断临时id是否为空
        if(!StringUtils.isEmpty(userTempId)){
            //获取key
            String cartKey = this.getKey(userTempId);
            //取值
            cartInfoList=redisTemplate.boundHashOps(cartKey).values();
        }

        //判断用户id是否为空
        if(!StringUtils.isEmpty(userId)){
            //获取key
            String cartKey = this.getKey(userId);
            //取值
            cartInfoList=redisTemplate.boundHashOps(cartKey).values();
        }

        //排序updateTime
        if(!CollectionUtils.isEmpty(cartInfoList)){
            cartInfoList.sort((o1,o2)->{
                return DateUtil.truncatedCompareTo(o1.getUpdateTime(),o2.getUpdateTime(), Calendar.SECOND);
            });
        }

        return cartInfoList;
    }

    //获取操作购物车的key  user:userId:cart
    private String getKey(String userId) {
        return RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
    }
}
