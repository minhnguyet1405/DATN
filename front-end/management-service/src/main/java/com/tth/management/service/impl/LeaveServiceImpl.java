package com.tth.management.service.impl;

import com.tth.management.model.Leave;
import com.tth.management.repository.LeaveRepository;
import com.tth.management.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveRepository leaveRepository;

    @Override
    public Page<Leave> getAllLeave(Pageable pageable, String userId, String status, Date startTime, Date endTime) {
        return leaveRepository.getAllLeave(userId,status,startTime,endTime,pageable);
    }

    @Override
    public void save(Leave leave) {
        leaveRepository.save(leave);
    }

    @Override
    public void delete(Leave leave) {
        leaveRepository.delete(leave);
    }

    @Override
    public List<Leave> findByIdIn(List<String> ids) {
        return leaveRepository.findByIdIn(ids);
    }

    @Override
    public Leave findById(String id) {
        return leaveRepository.findById(id).orElse(null);
    }
}
