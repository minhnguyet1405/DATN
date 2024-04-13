package com.elcom.its.upload.service;

import com.elcom.its.upload.service.upload.FileStorageConfig;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableConfigurationProperties({
    FileStorageConfig.class
})
@EnableCaching
@EnableScheduling
public class UploadServiceApplication {
    
    public static void main(String[] args) {
        // Fix lỗi "UDP failed setting ip_ttl | Method not implemented" khi start app trên Windows
        System.setProperty("java.net.preferIPv4Stack", "true");
        
        SpringApplication.run(UploadServiceApplication.class, args);

    }
}
