package com.tth.management.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tth.management.model.dto.User;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<User> getUser(String urlParam) throws IOException;
    User getDetailUser(String pathParam) throws IOException;
}
