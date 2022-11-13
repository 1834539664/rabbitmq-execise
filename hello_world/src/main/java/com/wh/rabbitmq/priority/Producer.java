package com.wh.rabbitmq.priority;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author WH
 * @version 1.0
 * @date 2022/11/9 16:17
 * 生产者 : 发消息
 */
public class Producer {
    //队列名称
    public static final String QUEUE_NAME = "hello";

    //发消息
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //工厂IP 连接RabbitMQ队列
        factory.setHost("192.168.36.100");
        //用户名
        factory.setUsername("root");
        //密码
        factory.setPassword("wanghong");

        //创建连接
        Connection connection = factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();
        /**
         * 生成一个队列
         * 参数:
         * 1.队列名称 2.队列里面的消息是否持久化,默认情况下消息存储在内存中,默认为true
         * 3.该队列是否只供一个消费者消费 是否进行消息的共享,默认为 false
         * 4.是否自动删除 最后一个消费者断开连接以后,该队列是否自动删除,如果为true就自动删除
         * 5.其他参数
         */
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-max-priority",10);//官方允许是0-255,此处设置为10,允许优先级范围为0-10
        channel.queueDeclare(QUEUE_NAME, true, false, false, map);
        //发消息
        for (int i = 1; i < 11; i++) {
            String message = "info" +i;
            if (i!=5 && i!=10){
                channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            }else {
                AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();
                channel.basicPublish("",QUEUE_NAME,properties,message.getBytes());
            }
        }
        System.out.println("消息发送完毕");



    }
}
