package com.tth.socket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocketServiceApplication {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(SocketServiceApplication.class, args);
    }
}
