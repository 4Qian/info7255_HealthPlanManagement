package com.example.demo.rabbitMQ;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class RabbitMQPublisher {
    public final static String QUEUE_NAME = "plan-elastic-search";
    public static void main(String[] argv) throws Exception {

    }

    /**
     * publisher/producer: enqueue
     * @param message
     * @throws Exception
     */
    public static void publishToQueue(String message) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // publish to the queue
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
