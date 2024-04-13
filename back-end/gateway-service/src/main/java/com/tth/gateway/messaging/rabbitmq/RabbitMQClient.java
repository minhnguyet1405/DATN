package com.tth.gateway.messaging.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQClient {

    private final Logger logger = LoggerFactory.getLogger(RabbitMQClient.class);

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    @Qualifier("directAutoDeleteQueue")
    private Queue directAutoDeleteQueue;

    public String callRpcService(String exchangeName, String queueName, String key, String msg) {
        try {
            logger.info("callRpcService - exchangeName: {}, queueName: {}, key : {}, msg: {}",
                    exchangeName, queueName, key, msg);
//            Queue
            Queue queue = new Queue(queueName);
            addQueue(queue);
//            Exchange
            DirectExchange exchange = new DirectExchange(exchangeName);
            addExchange(exchange);
//            Binding
            addBinding(queue, exchange, key);

            //Send msg
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            Message message = new Message(msg.getBytes("UTF-8"), messageProperties);
            Object obj = amqpTemplate.convertSendAndReceive(exchangeName, key, message);
            return (String) obj;
        } catch (Exception ex) {
            logger.error("callRpcService Exception >>> " + ex.toString());
            ex.printStackTrace();
        }
        return null;
    }

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
