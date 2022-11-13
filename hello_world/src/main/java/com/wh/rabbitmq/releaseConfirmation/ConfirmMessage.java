package com.wh.rabbitmq.releaseConfirmation;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;
import com.wh.rabbitmq.utils.RabbitMqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/10 12:11
 * 发布确认模式
 */
public class ConfirmMessage {
    //批量发消息的个数
    public static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        //1.单个发布确认
        //publishMessageIndividually();//用时616ms
        //2.批量发布确认
        //publishMessageBatch();//用时112ms
        //3.异步批量确认
        ConfirmMessage.publishMessageAsync();//耗时43ms
    }


    //单个确认
    public static void publishMessageIndividually() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();


        //开启时间
        long begin = System.currentTimeMillis();

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //单个消息确认
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息:" + i + "发送成功");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(MESSAGE_COUNT + " 条消息发布耗时: " + (end - begin) + "ms");
    }

    public static void publishMessageBatch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //开启时间
        long begin = System.currentTimeMillis();

        //批量确认消息的大小
        int batchSize = 100;
        //批量发消息 批量发布确认
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            if ((i + 1) % batchSize == 0) {
                channel.waitForConfirms();
            }

        }
        long end = System.currentTimeMillis();
        System.out.println(MESSAGE_COUNT + " 条消息发布耗时: " + (end - begin) + "ms");
    }

    //异步发布确认
    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();

        /**
         * 线程安全有序的哈希表 适用于高并发的情况
         * 功能:1.将序号与消息进行关联
         * 2.轻松的批量删除条目 只要给给到序号
         * 3.支持高并发(重点)
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();


        //准备消息的监听器,监听哪些消息成功了,哪些消息失败了
        //消息确认成功回调方法(函数)
        /**
         * 参数:
         * 1.消息的标记
         * 2.是否为批量确认
         */
        ConfirmCallback confirmCallback = (deliveryTag, multiple) -> {
            if (multiple) {
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            } else {
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("确认的消息:" + deliveryTag);
        };
        //消息确认失败回调方法(函数)
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认的消息:" + message + "  未确认消息tag:" + deliveryTag);
        };

        /**
         * 1.监听哪些消息成功了
         * 2.监听哪些消息失败了
         */
        channel.addConfirmListener(confirmCallback, nackCallback);

        //开启时间 发送消息
        long begin = System.currentTimeMillis();

        //批量发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "aaa";
            channel.basicPublish("", queueName, null, message.getBytes());
            //1.此处记录下所有要发送的消息
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
        }


        long end = System.currentTimeMillis();
        System.out.println(MESSAGE_COUNT + " 条消息发布耗时: " + (end - begin) + "ms");
    }
}
