package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

public interface OrderService {
    //提交订单
    Long submitOrder(OrderInfo orderInfo);

    //生成流水号
    String getTradeNo(String userId);

    //校验流水号
    boolean checkTradeCode(String userId,String tradeNoCode);

    //删除流水号
    void deleteTradeCode(String userId);
}
