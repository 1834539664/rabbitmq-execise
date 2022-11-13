package com.wh.rabbitmq.two;

import com.rabbitmq.client.Channel;
import com.wh.rabbitmq.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/9 21:38
 */
public class Task01 {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //从控制台接收信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            //发送消息
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("发送消息:" + message);
        }

        System.out.println("消息发送完毕");

    }
}
