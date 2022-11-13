package com.wh.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wh.rabbitmq.utils.RabbitMqUtils;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/11 23:23
 */
public class Consumer {
    public static final String QUEUE_NAME = "mirrior_hello";
    public static final String EXCHANGE_NAME = "fed_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare("node2_queue",true,false,false,null);
        channel.queueBind("node2_queue",EXCHANGE_NAME,"routeKey");
        DeliverCallback deliverCallback =((consumerTag, message) -> {
            System.out.println(new String(message.getBody()));
        });
        CancelCallback cancelCallback =consumerTag -> {
            System.out.println("消息被中断");
        };
        channel.basicConsume("node2_queue",true,deliverCallback,cancelCallback);
    }
}
