package com.wh.rabbitmq.dead_exchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.wh.rabbitmq.utils.RabbitMqUtils;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/10 18:24
 * 死信队列 生产者
 */
public class Producer {
    //普通交换机名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //死信消息 设置ttl时 time to live 单位是ms
        AMQP.BasicProperties properties =
                new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 1; i < 11; i++) {
            String message = "info" + i;
            //此处切记绑定properties
            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", null, message.getBytes());
        }
    }
}
