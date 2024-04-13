package com.tth.management.service;

import com.tth.management.model.CheckInOut;
import com.tth.management.model.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface LeaveService {
    Page<Leave> getAllLeave(Pageable pageable, String userId, String status, Date startTime, Date endTime);
    void save(Leave leave);
    void delete(Leave leave);
    List<Leave> findByIdIn(List<String>ids);
    Leave findById(String id);
}
