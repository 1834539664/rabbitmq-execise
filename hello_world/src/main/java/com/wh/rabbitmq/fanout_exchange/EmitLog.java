package com.wh.rabbitmq.fanout_exchange;

import com.rabbitmq.client.Channel;
import com.wh.rabbitmq.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/10 16:32
 * 发消息给交换机
 */
public class EmitLog {
    //交换机名称
    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明一个交换机 (发布订阅模式)
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println("生产者发出消息:" + message);
        }

    }
}
