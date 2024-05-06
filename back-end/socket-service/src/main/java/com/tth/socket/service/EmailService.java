package com.tth.socket.service;

public interface EmailService {
    public void sendEmail(String emailUsername,String emailPassword,String listReceiveEmail,String content);
}
