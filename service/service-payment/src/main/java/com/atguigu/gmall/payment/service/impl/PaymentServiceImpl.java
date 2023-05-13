package com.atguigu.gmall.payment.service.impl;

import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    //保存支付信息
    @Override
    public void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        //创建查询条件对象
        LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentInfo::getOrderId,orderInfo.getId());
        wrapper.eq(PaymentInfo::getPaymentType,paymentType);

        //判断支付记录信息是否存在
        Integer count = paymentInfoMapper.selectCount(wrapper);
        if(count>0){
            return;
        }
        //创建支付记录对象
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setSubject(orderInfo.getTradeBody());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        //保存支付记录
        paymentInfoMapper.insert(paymentInfo);
    }
}
