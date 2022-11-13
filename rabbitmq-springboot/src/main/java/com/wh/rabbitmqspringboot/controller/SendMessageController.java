package com.wh.rabbitmqspringboot.controller;

import com.wh.rabbitmqspringboot.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/11 11:27
 * 发送延迟消息
 */
@Slf4j
@RestController
@RequestMapping("ttl")
public class SendMessageController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    //开始发消息
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable String message){
        log.info("当前时间:{}, 发送一条信息给两个ttl队列:{}",new Date().toString(),message);
        rabbitTemplate.convertAndSend("X","XA","消息来自ttl为10s的队列:"+message);
        rabbitTemplate.convertAndSend("X","XB","消息来自ttl为40s的队列:"+message);
    }

    @GetMapping("sendExpirationMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable String message,@PathVariable String ttlTime) {
        rabbitTemplate.convertAndSend("X", "XC", message, correlationData ->{
            correlationData.getMessageProperties().setExpiration(ttlTime);
            return correlationData;
        });
        log.info("当前时间：{},发送一条时长{}毫秒 TTL 信息给队列 C:{}", new Date(),ttlTime, message);
    }

    //发送消息,基于插件的 延迟队列
    @GetMapping("/sendDelayMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable String message,@PathVariable Integer ttlTime){
        log.info("当前时间:{},发送一条时长{}毫秒信息给延迟队列delayed.queue:{}",new Date().toString(),ttlTime,message);
        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME,DelayedQueueConfig.DELAYED_ROUTING_KEY,message,msg->{
            //发送消息的延迟时长
            msg.getMessageProperties().setDelay(ttlTime);
            return msg;
        });
    }





}
