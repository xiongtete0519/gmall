package com.atguigu.gmall.mq.receiver;

import com.atguigu.gmall.mq.config.DelayedMqConfig;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
public class DelayedReceiver {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 实现幂等性思路：
     * 1、是否接收消息添加标记
     *      redis中的  setNx
     * 2、如果redis中存在了，一定是消费了吗？
     *  如果未消费 0
     *      消费  1
     *
     * @param msg
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = DelayedMqConfig.queue_delay_1)
    public void getMessage(String msg, Message message, Channel channel){

        String delayKey="delayed:";

        //设置消息到Redis --第一次：true
        Boolean result = redisTemplate.opsForValue().setIfAbsent(delayKey + msg, "0", 10, TimeUnit.MINUTES);
        //判断
        if(!result){
            //判断是否消费
            String flag = (String) redisTemplate.opsForValue().get(delayKey + msg);
            //判断
            if("1".equals(flag)){   //已经消费了

                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }else{
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                System.out.println("消息接收的时间:\t"+dateTimeFormatter.format(LocalDateTime.now()));
                System.out.println("消息的内容是:"+msg);
                //设置消息标识为1
                redisTemplate.opsForValue().set(delayKey+msg,"1",10,TimeUnit.MINUTES);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
        }else{
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("消息接收的时间:\t"+dateTimeFormatter.format(LocalDateTime.now()));
            System.out.println("消息的内容是:"+msg);
            //设置消息标识为1
            redisTemplate.opsForValue().set(delayKey+msg,"1",10,TimeUnit.MINUTES);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }
}
