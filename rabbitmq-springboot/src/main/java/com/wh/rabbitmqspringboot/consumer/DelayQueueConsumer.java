package com.wh.rabbitmqspringboot.consumer;

import com.rabbitmq.client.Channel;
import com.wh.rabbitmqspringboot.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/11 14:14
 * 消费者 基于插件的延迟消息
 */
@Slf4j
@Component
public class DelayQueueConsumer {
    //接收消息
    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE_NAME)
    public void receive(Message message) throws Exception {
        String msg = new String(message.getBody());
        log.info("当前时间:{},收到死信队列的消息:{}", new Date().toString(), msg);
    }
}
