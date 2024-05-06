package com.tth.management.service;
import java.io.IOException;
import java.util.Map;

public interface EmailService {
    boolean sendEmail (Map<String, String> headerParam, Map<String, Object> bodyParam) throws IOException;
}
