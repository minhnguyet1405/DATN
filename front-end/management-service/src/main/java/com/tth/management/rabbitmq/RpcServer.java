package com.tth.management.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tth.common.constant.ResourcePath;
import com.tth.common.message.RequestMessage;
import com.tth.common.message.ResponseMessage;
import com.tth.common.utils.StringUtil;
import com.tth.management.controller.CheckInOutController;
import com.tth.management.controller.LeaveController;
import com.tth.management.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class RpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    @Autowired
    private CheckInOutController checkInOutController;

    @Autowired
    private LeaveController leaveController;

    @RabbitListener(queues = "${management.rpc.queue}")
    public String processService(String json) throws ValidationException {
        try {
            LOGGER.info(" [-->] Server received request for " + json);
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.setDateFormat(df);
            RequestMessage request = mapper.readValue(json, RequestMessage.class);

            //Process here
            ResponseMessage response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null);
            if (request != null) {
                String requestPath = request.getRequestPath().replace(request.getVersion() != null
                        ? request.getVersion() : ResourcePath.VERSION, "");
                String urlParam = request.getUrlParam();
                String pathParam = request.getPathParam();
                Map<String, Object> bodyParam = request.getBodyParam();
                Map<String, String> headerParam = request.getHeaderParam();
                //GatewayDebugUtil.debug(requestPath, urlParam, pathParam, bodyParam, headerParam);
                LOGGER.info(" [-->] Server received requestPath =========>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + requestPath);

                switch (request.getRequestMethod()) {
                    case "GET":
                        if("/management/check-in-out".equalsIgnoreCase(requestPath)){
                            response = checkInOutController.getAllDepartment(urlParam, headerParam);
                        }
                        else if("/management/leave".equalsIgnoreCase(requestPath)){
                            response = leaveController.getAllLeave(urlParam, headerParam);
                        }
                        break;
                    case "POST":
                       if("/management/check-in-out".equalsIgnoreCase(requestPath)){
                            response = checkInOutController.createCheckInOut(headerParam, bodyParam);
                        }
                       else if("/management/leave".equalsIgnoreCase(requestPath)){
                            response = leaveController.createLeave(headerParam, bodyParam);
                        }
                        break;
                    case "PUT":
                        if("/management/leave".equalsIgnoreCase(requestPath)){
                            response = leaveController.updateLeave(headerParam, bodyParam);
                        }
                        break;
                    case "DELETE":
                        if("/management/leave".equalsIgnoreCase(requestPath)){
                            response = leaveController.deleteLeave( headerParam, bodyParam);
                        }
                        break;
                    default:
                        break;
                }
            }
            LOGGER.info(" [<--] Server returned " + response != null ? response.toJsonString() : "null");
            return response != null ? response.toJsonString() : null;
        } catch (Exception ex) {
            LOGGER.error("Error to processService >>> " + StringUtil.printException(ex));
            ex.printStackTrace();
        }
        return null;
    }
}