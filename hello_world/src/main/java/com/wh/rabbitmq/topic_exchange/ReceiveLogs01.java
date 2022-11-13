package com.wh.rabbitmq.topic_exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wh.rabbitmq.utils.RabbitMqUtils;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/10 16:17
 */
public class ReceiveLogs01 {
    //交换机名称
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        /**
         * 生成一个临时队列,队列名称是随机的
         * 当消费者断开与队列的连接的时候 队列就自动删除
         */
        String queueName = channel.queueDeclare().getQueue();
        //绑定队列与交换机
        channel.queueBind(queueName, EXCHANGE_NAME, "*.orange.*");
        System.out.println("Q1 等待接收消息,把接收到的消息打印在控制台......");

        //接收消息回调函数
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("Q1 控制台打印接收到的消息:" + new String(message.getBody(), "UTF-8"));
            System.out.println("绑定的键" + message.getEnvelope().getRoutingKey());
        };
        //消费消息
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });

    }
}
