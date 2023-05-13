package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.order.OrderInfo;

public interface PaymentService {
    //保存支付信息
    void savePaymentInfo(OrderInfo orderInfo, String paymentType);
}
