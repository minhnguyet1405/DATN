package com.tth.management.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tth.common.constant.ResourcePath;
import com.tth.common.message.RequestMessage;
import com.tth.common.message.ResponseMessage;
import com.tth.management.model.dto.User;
import com.tth.management.rabbitmq.RabbitMQClient;
import com.tth.management.rabbitmq.RabbitMQProperties;
import com.tth.management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private RabbitMQClient rabbitMQClient;

    @Override
    public boolean sendEmail(Map<String, String> headerParam, Map<String, Object> bodyParam) throws IOException {
        RequestMessage rbacRpcRequest = new RequestMessage();
        rbacRpcRequest.setRequestMethod("POST");
        rbacRpcRequest.setRequestPath("/socket/send-email");
        rbacRpcRequest.setBodyParam(bodyParam);
        rbacRpcRequest.setUrlParam(null);
        rbacRpcRequest.setHeaderParam(headerParam);
        rbacRpcRequest.setVersion(ResourcePath.VERSION);
        String result = rabbitMQClient.callRpcService(RabbitMQProperties.SOCKET_RPC_EXCHANGE,
                RabbitMQProperties.SOCKET_RPC_QUEUE, RabbitMQProperties.SOCKET_RPC_KEY, rbacRpcRequest.toJsonString());
        if (result != null) {
            ObjectMapper mapper = new ObjectMapper();
            ResponseMessage resultResponse = null;
            resultResponse = mapper.readValue(result, ResponseMessage.class);
            if (resultResponse != null && resultResponse.getStatus() == HttpStatus.OK.value() && resultResponse.getData() != null) {
                return true;
            }
        }
        return false;
    }
}
