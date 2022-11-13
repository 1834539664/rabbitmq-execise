package com.wh.rabbitmqspringboot.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/11 16:03
 */
@Component
@Slf4j
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    //注入
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }
    /**
     * 交换机确认回调方法
     * @param correlationData 保存回调的id及相关消息
     * @param ack 交换机收到信息 true 未收到 false
     * @param cause null 或 失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack){
            log.info("交换机已经收到了id为:{}的消息",id);
        }else {
            log.info("交换机未收到id为:{}的消息,原因是:{}",id,cause);
        }
    }

    //当消息传递过程中不可达目的地时将消息返回给生产者。
    //只有不可达目的地的时候才进行
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        String msg = new String(returned.getMessage().getBody());
        log.error("消息{},被交换机{}退回,退回原因:{},路由Key:{}",msg,returned.getExchange(),returned.getReplyText(),returned.getRoutingKey());
    }


}
