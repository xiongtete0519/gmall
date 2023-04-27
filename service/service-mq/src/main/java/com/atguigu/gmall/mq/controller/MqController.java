package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.mq.config.DeadLetterMqConfig;
import com.atguigu.gmall.mq.config.DelayedMqConfig;
import com.atguigu.gmall.service.RabbitService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/mq")
public class MqController {

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("sendDelayed")
    public Result sendDelayed(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //封装后的api
        rabbitService.sendDelayedMessage(DelayedMqConfig.exchange_delay,DelayedMqConfig.routing_delay,"我是延迟插件的消息",10);
        System.out.println("延迟插件消息发送时间：\t"+dateTimeFormatter.format(LocalDateTime.now()));
        //        rabbitTemplate.convertAndSend(DelayedMqConfig.exchange_delay,
//                DelayedMqConfig.routing_delay,
//                "我是延迟插件的消息", new MessagePostProcessor() {
//                    @Override
//                    public Message postProcessMessage(Message message) throws AmqpException {
//
//                        //设置消息的延迟时间
//                        message.getMessageProperties().setDelay(10*1000);
//
//                        System.out.println("延迟插件消息发送时间：\t"+dateTimeFormatter.format(LocalDateTime.now()));
//                        return message;
//                    }
//                });


        return Result.ok();
    }

    //发送延迟消息-死信队列
    @GetMapping("/sendDeadLetter")
    public Result sendDeadLetter(){
        //时间格式化
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        rabbitService.sendMessage(DeadLetterMqConfig.exchange_dead,DeadLetterMqConfig.routing_dead_1,"我是延迟消息");

        System.out.println("消息发送的时间:\t"+dateTimeFormatter.format(LocalDateTime.now()));
        return Result.ok();
    }


    //发送消息的方法
    @GetMapping("/send")
    public Result send(){
        rabbitService.sendMessage("exchange.confirm888","routingKey.confirm","你好，我是消息，我来了");
        return Result.ok();
    }

}
