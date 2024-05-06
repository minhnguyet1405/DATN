package com.tth.socket.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProperties {

    //User service
    @Value("${user.rpc.exchange}")
    public static String USER_RPC_EXCHANGE;

    @Value("${user.rpc.queue}")
    public static String USER_RPC_QUEUE;

    @Value("${user.rpc.key}")
    public static String USER_RPC_KEY;

    @Value("${user.rpc.authen.url}")
    public static String USER_RPC_AUTHEN_URL;

    //vehicle service
    @Value("${socket.rpc.exchange}")
    public static String SOCKET_RPC_EXCHANGE;

    @Value("${socket.rpc.queue}")
    public static String SOCKET_RPC_QUEUE;

    @Value("${socket.rpc.key}")
    public static String SOCKET_RPC_KEY;

    @Autowired
    public RabbitMQProperties(@Value("${user.rpc.exchange}") String userRpcExchange,
                              @Value("${user.rpc.queue}") String userRpcQueue,
                              @Value("${user.rpc.key}") String userRpcKey,
                              @Value("${user.rpc.authen.url}") String userRpcAuthenUrl,
                              @Value("${socket.rpc.exchange}") String socketRpcExchange,
                              @Value("${socket.rpc.queue}") String socketRpcQueue,
                              @Value("${socket.rpc.key}") String socketRpcKey){
        //user
        USER_RPC_EXCHANGE = userRpcExchange;
        USER_RPC_QUEUE = userRpcQueue;
        USER_RPC_KEY = userRpcKey;
        USER_RPC_AUTHEN_URL = userRpcAuthenUrl;

        //vehicle
        SOCKET_RPC_EXCHANGE = socketRpcExchange;
        SOCKET_RPC_QUEUE = socketRpcQueue;
        SOCKET_RPC_KEY = socketRpcKey;
    }
}
