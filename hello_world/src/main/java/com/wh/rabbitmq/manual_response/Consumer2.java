package com.wh.rabbitmq.manual_response;

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
public class Consumer2 {
    //队列名称
    public static final String QUEUE_NAME = "ack_queue";

    //接收消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //声明 接收消息的回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            //沉睡1s
            try {
                Thread.sleep(2*1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("consumer2 接收消息: " + new String(message.getBody()));
            //手动应答
            /**
             * 参数:
             * 1.消息的标记  2.是否批量应答 false:不批量应答
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);

        };
        //声明 取消消息时的回调
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("consumer2 消息消费被中断");
        };
        //消息的接收
        System.out.println("consumer2 等待接收消息...");
        //设置不公平分发 1 为不公平分发(能者多劳) 0 为公平分发(平均分发),默认为0
        channel.basicQos(2);
        //设置手动接收
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
