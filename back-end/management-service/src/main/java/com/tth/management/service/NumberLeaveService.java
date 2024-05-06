package com.tth.management.service;

import com.tth.management.model.NumberLeave;

public interface NumberLeaveService {
    NumberLeave findByUserId(String userId);
}
