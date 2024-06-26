package com.tth.socket.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQClient {

    private final Logger LOGGER = LoggerFactory.getLogger(RabbitMQClient.class);

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    @Qualifier("directAutoDeleteQueue")
    private Queue directAutoDeleteQueue;

    //@Async
    public String callRpcService(String exchangeName, String queueName, String key, String msg) {
        LOGGER.info("callRpcService - exchangeName: " + exchangeName + ", queueName: " + queueName + ", key : " + key);
        //Queue
        Queue queue = new Queue(queueName);
        addQueue(queue);
        //Exchange
        DirectExchange exchange = new DirectExchange(exchangeName);
        addExchange(exchange);
        //Binding
        addBinding(queue, exchange, key);

        //Send msg
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message = new Message(msg.getBytes(), messageProperties);
        return (String) amqpTemplate.convertSendAndReceive(exchangeName, key, message);
    }

//    //@Async
//    public boolean callPublishService(String exchangeName, String key, String msg) {
//        LOGGER.info("callPublishService - exchangeName: {}, key : {}", exchangeName, key);
//        //Exchange
//        DirectExchange exchange = new DirectExchange(exchangeName);
//        addExchange(exchange);
//        //Binding
//        addBinding(directAutoDeleteQueue, exchange, key);
//
//        //Send msg
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
//        Message message = new Message(msg.getBytes(), messageProperties);
//
//        try {
//            amqpTemplate.convertAndSend(exchangeName, key, message);
//        } catch (AmqpException ex) {
//            ex.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    //@Async
//    public boolean callWorkerService(String queueName, String msg) {
//        LOGGER.info("callWorkerService - queueName : {}", queueName);
//        //Queue
//        Queue queue = new Queue(queueName);
//        addQueue(queue);
//
//        //Send msg
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
//        Message message = new Message(msg.getBytes(), messageProperties);
//        try {
//            amqpTemplate.convertAndSend(queueName, message);
//        } catch (AmqpException ex) {
//            ex.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    private String addQueue(Queue queue) {
        return amqpAdmin.declareQueue(queue);
    }

    private void addExchange(AbstractExchange exchange) {
        amqpAdmin.declareExchange(exchange);
    }

    private void addBinding(Queue queue, DirectExchange exchange, String routingKey) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
        amqpAdmin.declareBinding(binding);
    }
}