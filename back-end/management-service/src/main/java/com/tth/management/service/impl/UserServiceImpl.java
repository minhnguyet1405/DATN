package com.tth.management.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tth.common.constant.ResourcePath;
import com.tth.common.message.RequestMessage;
import com.tth.common.message.ResponseMessage;
import com.tth.management.model.dto.User;
import com.tth.management.rabbitmq.RabbitMQClient;
import com.tth.management.rabbitmq.RabbitMQProperties;
import com.tth.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RabbitMQClient rabbitMQClient;

    @Override
    public List<User> getUser(String urlParam) throws IOException {
        RequestMessage rbacRpcRequest = new RequestMessage();
        rbacRpcRequest.setRequestMethod("GET");
        rbacRpcRequest.setRequestPath("/user/internal");
        rbacRpcRequest.setBodyParam(null);
        rbacRpcRequest.setUrlParam(null);
        rbacRpcRequest.setHeaderParam(null);
        rbacRpcRequest.setVersion(ResourcePath.VERSION);
        String result = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rbacRpcRequest.toJsonString());
        if (result != null) {
            ObjectMapper mapper = new ObjectMapper();
            ResponseMessage resultResponse = null;
                resultResponse = mapper.readValue(result, ResponseMessage.class);
                if (resultResponse != null && resultResponse.getStatus() == HttpStatus.OK.value() && resultResponse.getData() != null) {
                    //OK
                    JsonNode jsonNode = mapper.readTree(result);
                    List<User> userIdDTOList = mapper.readerFor(new TypeReference<List<User>>() {
                    }).readValue(jsonNode.get("data").get("data"));
                    return userIdDTOList;
                }
        }
        return null;
    }

    @Override
    public User getDetailUser(String pathParam) throws IOException {
        RequestMessage rbacRpcRequest = new RequestMessage();
        rbacRpcRequest.setRequestMethod("GET");
        rbacRpcRequest.setRequestPath("/user/detail");
        rbacRpcRequest.setPathParam(pathParam);
        rbacRpcRequest.setBodyParam(null);
        rbacRpcRequest.setUrlParam(null);
        rbacRpcRequest.setHeaderParam(null);
        rbacRpcRequest.setVersion(ResourcePath.VERSION);
        String result = rabbitMQClient.callRpcService(RabbitMQProperties.USER_RPC_EXCHANGE,
                RabbitMQProperties.USER_RPC_QUEUE, RabbitMQProperties.USER_RPC_KEY, rbacRpcRequest.toJsonString());
        if (result != null) {
            ObjectMapper mapper = new ObjectMapper();
            ResponseMessage resultResponse = null;
            resultResponse = mapper.readValue(result, ResponseMessage.class);
            if (resultResponse != null && resultResponse.getStatus() == HttpStatus.OK.value() && resultResponse.getData() != null) {
                //OK
                JsonNode jsonNode = mapper.readTree(result);
                User userIdDTOList = mapper.readerFor(new TypeReference<User>() {
                }).readValue(jsonNode.get("data").get("data"));
                return userIdDTOList;
            }
        }
        return null;
    }
}
