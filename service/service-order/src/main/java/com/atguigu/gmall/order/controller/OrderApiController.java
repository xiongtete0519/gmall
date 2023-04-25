package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.user.client.UserFeignClient;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@SuppressWarnings("all")
public class OrderApiController {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private OrderService orderService;

    @ApiOperation("提交订单")
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo,HttpServletRequest request){

        //获取用户id
        String userId = AuthContextHolder.getUserId(request);
        orderInfo.setUserId(Long.parseLong(userId));

        Long orderId=orderService.submitOrder(orderInfo);
        return Result.ok(orderId);
    }

    @ApiOperation("去结算")
    @GetMapping("/auth/trade")
    public Result trade(HttpServletRequest request){
        //创建Map封装数据
        Map<String,Object> resultMap=new HashMap<>();

        //获取用户id
        String userId = AuthContextHolder.getUserId(request);
        //查询地址列表
        List<UserAddress> userAddressListByUserId = userFeignClient.findUserAddressListByUserId(Long.parseLong(userId));
        //封装购物列表
        List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);
        List<OrderDetail> orderDetailList=null;
        //判断
        if(!CollectionUtils.isEmpty(cartCheckedList)){
                orderDetailList = cartCheckedList.stream().map(cartInfo -> {
                //创建订单明细对象
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(productFeignClient.getSkuPrice(cartInfo.getSkuId()));
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                return orderDetail;
            }).collect(Collectors.toList());
        }
        //数量
//        int skuNum = orderDetailList.size();
        int skuNum =0;
        for (OrderDetail orderDetail : orderDetailList) {
            skuNum+=orderDetail.getSkuNum();
        }
        //总金额
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailList);
        //计算总金额--在计算之前必须设置订单明细集合
        orderInfo.sumTotalAmount();
        resultMap.put("userAddressList",userAddressListByUserId);
        resultMap.put("detailArrayList",orderDetailList);
        resultMap.put("totalNum",skuNum);
        resultMap.put("totalAmount",orderInfo.getTotalAmount());


        return Result.ok(resultMap);
    }


}
