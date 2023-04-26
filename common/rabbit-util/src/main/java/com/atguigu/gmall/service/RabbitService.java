package com.atguigu.gmall.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //发送消息封装
    public boolean sendMessage(String exchange,String routingKey,Object message){
        //发送消息
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        return true;
    }

}
