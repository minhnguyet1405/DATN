package com.tth.socket.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcConfig {

    @Value("${user.rpc.exchange}")
    private String rpcExchange;

    @Value("${user.rpc.queue}")
    private String rpcQueue;

    @Value("${user.rpc.key}")
    private String rpcKey;

    @Bean("rpcQueue")
    public Queue rpcQueue() {
        return new Queue(rpcQueue);
    }

    @Bean("rpcExchange")
    public DirectExchange rpcExchange() {
        return new DirectExchange(rpcExchange);
    }

    @Bean("rpcBinding")
    public Binding bindingRpc(DirectExchange rpcExchange, Queue rpcQueue) {
        return BindingBuilder.bind(rpcQueue).to(rpcExchange).with(rpcKey);
    }

    @Bean
    public RpcServer rpcServer() {
        return new RpcServer();
    }
}