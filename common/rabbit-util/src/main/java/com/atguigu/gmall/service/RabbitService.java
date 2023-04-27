package com.atguigu.gmall.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.model.GmallCorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    //发送延迟消息
    public boolean sendDelayedMessage(String exchange, String routingKey,Object message,int delayTime){


        //重试机制-封装对象
        //创建实体类封装消息信息
        GmallCorrelationData gmallCorrelationData=new GmallCorrelationData();

        //设置id
        String correlationDataId = UUID.randomUUID().toString().replaceAll("-", "");
        gmallCorrelationData.setId(correlationDataId);
        //设置消息
        gmallCorrelationData.setMessage(message);
        //设置交换机
        gmallCorrelationData.setExchange(exchange);
        //设置路由
        gmallCorrelationData.setRoutingKey(routingKey);
        //是否延迟
        gmallCorrelationData.setDelay(true);
        //延迟时间
        gmallCorrelationData.setDelayTime(delayTime);

        //存储到redis
        redisTemplate.opsForValue().set(correlationDataId, JSON.toJSONString(gmallCorrelationData));

        rabbitTemplate.convertAndSend(exchange,routingKey,message,message1 -> {

            //设置延迟时间
            message1.getMessageProperties().setDelay(delayTime*1000);
            return message1;
        },gmallCorrelationData);

        return true;


    }

    //发送消息封装
    public boolean sendMessage(String exchange,String routingKey,Object message){
        //创建实体类，封装消息信息
        GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();
        //设置id
        String correlationDataId = UUID.randomUUID().toString().replaceAll("-", "");
        gmallCorrelationData.setId(correlationDataId);
        //设置消息
        gmallCorrelationData.setMessage(message);
        //设置交换机
        gmallCorrelationData.setExchange(exchange);
        //设置路由
        gmallCorrelationData.setRoutingKey(routingKey);

        //存储到redis
        redisTemplate.opsForValue().set(
                correlationDataId,
                JSON.toJSONString(gmallCorrelationData),
                10,
                TimeUnit.MINUTES);
        //发送消息
//        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        rabbitTemplate.convertAndSend(exchange,routingKey,message,gmallCorrelationData);
        return true;
    }

}
