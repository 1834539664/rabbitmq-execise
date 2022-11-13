package com.wh.rabbitmq.two;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wh.rabbitmq.utils.RabbitMqUtils;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/9 21:12
 * 这是一个工作线程(一个消费者)
 */
public class Worker01 {
    //队列名称
    public static final String QUEUE_NAME = "hello";

    //接收消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //声明 接收消息的回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("worker01 接收消息: " + new String(message.getBody()));
        };
        //声明 取消消息时的回调
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("worker01 消息消费被中断");
        };
        //消息的接收
        System.out.println("worker01 等待接收消息...");

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
