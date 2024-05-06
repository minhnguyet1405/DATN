/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tth.socket.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author Admin
 */
public class EmailUtil {

    public static void sendGmail(String sourceEmail, String sourcePassword, String listReceiveEmail,
            String content) {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sourceEmail, sourcePassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sourceEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(listReceiveEmail)
            );

            message.setSubject("ITS NT-CL: Cảnh báo dữ liệu lúc " + new Date());
            message.setText("Cảnh báo : \n" + content
                    + "\n"
                    + "\n Vui lòng không phản hồi email này!");
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
