package com.wh.rabbitmq.manual_response;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.wh.rabbitmq.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/9 21:38
 * 手动应答,消息在手动应答时不消失
 */
public class Task02 {
    //队列名称
    public static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明队列
        //队列持久化,队列不丢失
        Boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

        //随机消息
        int num = 7;
        for (int i = 1; i <= num; i++) {
            String message = i + "";
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            System.out.println("发送消息:" + message);
        }
        System.out.println("消息发送完毕");

    }
}
