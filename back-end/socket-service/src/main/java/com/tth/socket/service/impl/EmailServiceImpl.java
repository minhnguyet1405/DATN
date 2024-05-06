package com.tth.socket.service.impl;

import com.tth.socket.service.EmailService;
import com.tth.socket.utils.EmailUtil;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(String emailUsername, String emailPassword, String listReceiveEmail, String content) {
        EmailUtil.sendGmail(emailUsername, emailPassword, listReceiveEmail, content);
    }
}
