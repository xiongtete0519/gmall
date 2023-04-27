package com.atguigu.gmall.mq.receiver;

import com.atguigu.gmall.mq.config.DeadLetterMqConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DeadLetterReceiver {


    @RabbitListener(queues = DeadLetterMqConfig.queue_dead_2)
    public void getMessage(String msg, Message message, Channel channel){

        //时间格式化
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("消息接收的时间：\t"+dateTimeFormatter.format(LocalDateTime.now()));

        System.out.println("消息的内容"+msg);

    }
}
