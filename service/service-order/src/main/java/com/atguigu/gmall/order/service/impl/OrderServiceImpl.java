package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.PaymentWay;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
@SuppressWarnings("all")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 提交订单
     * 涉及到的表：
     * order_info 订单详情表 --订单的说明
     * order_detail 订单详情表   --商品的说明
     * <p>
     * 一个订单对应多个订单明细
     * 关联关系：orderDetailList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitOrder(OrderInfo orderInfo) {

        //总金额
        orderInfo.sumTotalAmount();
        //订单状态
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        //付款方式
        orderInfo.setPaymentWay(PaymentWay.ONLINE.name());
        //订单交易号
        String outTradeNo = "atguigu" + UUID.randomUUID().toString().replaceAll("-", "");
        orderInfo.setOutTradeNo(outTradeNo);
        StringBuilder tradeBody=new StringBuilder();
        //订单描述
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            tradeBody.append(orderDetail.getSkuNum()+"  ");
        }
        //设置订单描述
        if(tradeBody.toString().length()>100){
            orderInfo.setTradeBody(tradeBody.toString().substring(0,100));
        }else{
            orderInfo.setTradeBody(tradeBody.toString());
        }
        //操作时间
        orderInfo.setOperateTime(new Date());
        //失效时间+1天
        Calendar calendar = Calendar.getInstance();
        //天数+1
        calendar.add(Calendar.DATE,1);
        //设置超时时间
        orderInfo.setExpireTime(calendar.getTime());
        //设置订单进度状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());


        //保存订单
        orderInfoMapper.insert(orderInfo);

        //保存订单明细
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            //设置订单id
            orderDetail.setOrderId(orderInfo.getId());

            //添加
            orderDetailMapper.insert(orderDetail);
        }

        //返回订单id
        return orderInfo.getId();
    }
}
