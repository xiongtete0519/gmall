package com.atguigu.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class OrderReceiver {

    @Autowired
    private OrderService orderService;

    /**
     * 订单超时，判断订单状态
     * 已支付：不进行操作
     * 未支付：修改状态
     * @param orderId
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = MqConst.QUEUE_ORDER_CANCEL)
    public void cancelOrderStatus(Long orderId, Message message, Channel channel){
        //判断
        try {
            if(orderId!=null){
                //查询订单，根据id
                //先写查询订单的service，调用
                //使用通用service-继承接口，实现
                OrderInfo orderInfo = orderService.getById(orderId);
                //判断是否为空
                if(orderInfo!=null){
                    //获取状态进行判断
                    if("UNPAID".equals(orderInfo.getOrderStatus())&&"UNPAID".equals(orderInfo.getProcessStatus())){
                        //调用支付关闭订单
                        //处理超时订单
                        orderService.execExpiredOrder(orderId);
                    }

                }

            }
        } catch (Exception e) {
            //写入日志，数据库，短信
            e.printStackTrace();
        }
        //手动确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);


    }

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value =@Queue(value = MqConst.QUEUE_PAYMENT_PAY,durable = "true",autoDelete = "false"),
            exchange =@Exchange(value =MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,autoDelete = "false"),
            key ={MqConst.ROUTING_PAYMENT_PAY}
    ))
    public void paySuccess(Long orderId,Message message,Channel channel){
        try {
            //判断
            if(orderId!=null){
                //查询状态
                OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
                if(orderInfo!=null&&"UNPAID".equals(orderInfo.getOrderStatus())){
                    //修改状态
                    orderService.updateOrderStatus(orderId, ProcessStatus.PAID);

                    //发送消息，扣减库存
                    orderService.sendOrderStatus(orderInfo);
                }
            }
        } catch (Exception e) {
            //日志，短信通知
            e.printStackTrace();
        }


        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    //库存扣减成功，更新操作
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value =@Queue(value = MqConst.QUEUE_WARE_ORDER,durable = "true",autoDelete = "false"),
            exchange =@Exchange(value =MqConst.EXCHANGE_DIRECT_WARE_ORDER,autoDelete = "false"),
            key ={MqConst.ROUTING_WARE_ORDER}
    ))
    public void stockOrderStatus(String strJSON,Message message,Channel channel){

        try {
            //判断
            if(!StringUtils.isEmpty(strJSON)){
                //转换数据类型
                Map<String,String> map = JSON.parseObject(strJSON, Map.class);
                //获取订单id
                String orderId = map.get("orderId");
                //获取状态
                String status = map.get("status");
                //判断是否扣减成功
                if("DEDUCTED".equals(status)){
                    orderService.updateOrderStatus(Long.parseLong(orderId),ProcessStatus.WAITING_DELEVER);
                }else{
                    orderService.updateOrderStatus(Long.parseLong(orderId),ProcessStatus.STOCK_EXCEPTION);
                    //通知管理员，人工客服，商家
                }
            }
        } catch (NumberFormatException e) {
            //出现异常，邮件、短信等通知
            e.printStackTrace();
        }

        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
