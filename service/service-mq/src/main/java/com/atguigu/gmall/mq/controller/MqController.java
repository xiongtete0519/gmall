package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.mq.config.DeadLetterMqConfig;
import com.atguigu.gmall.service.RabbitService;
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
