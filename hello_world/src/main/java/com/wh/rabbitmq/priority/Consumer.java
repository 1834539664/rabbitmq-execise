package com.wh.rabbitmq.priority;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/9 20:32
 * 消费者,接收消息的
 */
public class Consumer {
    //队列名称
    public static final String QUEUE_NAME = "hello";
    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工程
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.36.100");
        factory.setUsername("root");
        factory.setPassword("wanghong");
        //建立连接
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明 接收消息的回调
        DeliverCallback deliverCallback = (consumerTag,message) -> {
            System.out.println(new String(message.getBody()));
        };
        //声明 取消消息时的回调
        CancelCallback cancelCallback = consumerTag ->{
            System.out.println("消息消费被中断");
        };

        /**
         * 消费者接收(消费)消息
         * 参数:
         * 1.消费哪个队列 2.消费成功后是否要自动应答 true代表自动应答 false代表手动应答
         * 3.消费者未成功消费的回调
         * 4.消费者取消消费的回调
         */
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
