package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

public interface OrderService {
    //提交订单
    Long submitOrder(OrderInfo orderInfo);
}
