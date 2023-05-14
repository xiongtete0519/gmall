package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
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
        PaymentInfo paymentInfo1 = this.getPaymentInfo(outTradeNo, name);
        if(paymentInfo1==null){
            return;
        }
        try {
            //创建对象
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setTradeNo(paramsMap.get("trade_no"));
            paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setCallbackContent(JSON.toJSONString(paramsMap));

            //设置条件对象
            LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PaymentInfo::getOutTradeNo,outTradeNo);
            wrapper.eq(PaymentInfo::getPaymentType,name);
            paymentInfoMapper.update(paymentInfo,wrapper);
        } catch (Exception e) {
            redisTemplate.delete(paramsMap.get("notify_id"));
            e.printStackTrace();
        }
    }
}
