package com.tth.socket.controller;

import com.tth.common.message.MessageContent;
import com.tth.common.message.ResponseMessage;
import com.tth.socket.model.dto.AuthorizationResponseDTO;
import com.tth.socket.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.Map;

@Controller
public class EmailController extends BaseController {
    @Autowired
    public JavaMailSender mailSender;

    public ResponseMessage

    sendEmail(Map<String, String> headerParam,
                                     Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            String email = (String) bodyParam.get("email");
            String uuid = (String) bodyParam.get("uuid");
            String name = (String) bodyParam.get("name");
            sendEmail(email, name, uuid);
            response = new ResponseMessage(HttpStatus.OK.value(), "Đăng ký nghỉ thành công",
                    new MessageContent(HttpStatus.OK.value(), "Đăng ký nghỉ thành công", null));
        }
        return response;
    }

    public String sendEmail(String email, String name, String uuid){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Đăng ký vắng mặt - " + name);
        message.setText("http://localhost:9527/#/approve/index");
        mailSender.send(message);
        return "Email Sent!";
    }
}
