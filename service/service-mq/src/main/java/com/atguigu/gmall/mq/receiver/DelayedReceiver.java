package com.atguigu.gmall.mq.receiver;

import com.atguigu.gmall.mq.config.DelayedMqConfig;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DelayedReceiver {

    @SneakyThrows
    @RabbitListener(queues = DelayedMqConfig.queue_delay_1)
    public void getMessage(String msg, Message message, Channel channel){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("消息接收的时间:\t"+dateTimeFormatter.format(LocalDateTime.now()));
        System.out.println("消息的内容是:"+msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
