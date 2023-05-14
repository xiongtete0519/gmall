package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    //保存支付信息
    void savePaymentInfo(OrderInfo orderInfo, String paymentType);

    //查询支付记录
    PaymentInfo getPaymentInfo(String outTradeNo, String name);

    //修改支付记录状态
    void updatePaymentInfo(String outTradeNo, String name, Map<String, String> paramsMap);
    void updatePaymentInfoStatus(String outTradeNo, String name, PaymentInfo paymentInfo);
}
