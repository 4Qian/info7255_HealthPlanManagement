package com.example.demo.rabbitMQ;

import com.example.demo.elasticsearch.ESService;
import com.example.demo.model.ESRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitMQConsumer {
    private final static String QUEUE_NAME = RabbitMQPublisher.QUEUE_NAME;
    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * consumer/subscriber: dequeue
     * @throws Exception
     */
    public static void start() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // from the bytes, we get the json string of the elastic search request
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            ESRequest request = objectMapper.readValue(message, ESRequest.class);
            try {
                ESService.processRequest(request);
            } catch (Exception e) {
                System.out.println("++++ catch elastic search exception");
            }
        };
        // once the queue has a new message, the message will be dequeued and the callback will be invoked
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
