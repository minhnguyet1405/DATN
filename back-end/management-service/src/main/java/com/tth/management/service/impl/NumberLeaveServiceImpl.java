package com.tth.management.service.impl;

import com.tth.management.model.NumberLeave;
import com.tth.management.repository.NumberLeaveRepository;
import com.tth.management.service.NumberLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NumberLeaveServiceImpl implements NumberLeaveService {
    @Autowired
    private NumberLeaveRepository numberLeaveRepository;

    @Override
    public NumberLeave findByUserId(String userId) {
        return numberLeaveRepository.findFirstByUserId(userId);
    }
}
