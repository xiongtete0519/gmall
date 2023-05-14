package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.atguigu.gmall.service.RabbitService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitService rabbitService;


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

    //查询支付记录
    @Override
    public PaymentInfo getPaymentInfo(String outTradeNo, String name) {
        LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentInfo::getOutTradeNo,outTradeNo);
        wrapper.eq(PaymentInfo::getPaymentType,name);
        return paymentInfoMapper.selectOne(wrapper);
    }

    //修改支付记录状态
    @Override
    public void updatePaymentInfo(String outTradeNo, String name, Map<String, String> paramsMap) {
        //查询判断
        PaymentInfo paymentInfoQuery = this.getPaymentInfo(outTradeNo, name);
        if(paymentInfoQuery==null){
            return;
        }
        try {
            //创建对象
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setTradeNo(paramsMap.get("trade_no"));
            paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setCallbackContent(JSON.toJSONString(paramsMap));
            updatePaymentInfoStatus(outTradeNo, name, paymentInfo);

        } catch (Exception e) {
            redisTemplate.delete(paramsMap.get("notify_id"));
            e.printStackTrace();
        }
        //修改订单状态 --消息队列
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,
                MqConst.ROUTING_PAYMENT_PAY,
                paymentInfoQuery.getOrderId());
    }

    /**
     * 修改支付记录状态
     * @param outTradeNo
     * @param name
     * @param paymentInfo
     */
    public void updatePaymentInfoStatus(String outTradeNo, String name, PaymentInfo paymentInfo) {
        //设置条件对象
        LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentInfo::getOutTradeNo,outTradeNo);
        wrapper.eq(PaymentInfo::getPaymentType,name);
        paymentInfoMapper.update(paymentInfo,wrapper);
    }
}
