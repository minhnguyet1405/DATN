package com.tth.management.service;

import com.tth.management.model.CheckInOut;
import com.tth.management.model.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface LeaveService {
    List<Leave> getAllLeave(String userId, String status, Date startTime, Date endTime);
    Page<Leave> getAllLeave(Pageable pageable, List<String> userIds, String type, Date startTime, Date endTime);
    void save(Leave leave);
    void delete(Leave leave);
    List<Leave> findByIdIn(List<String>ids);
    Leave findById(String id);
    List<Leave> transform(List<Leave> leaves) throws ParseException;
    Leave getLeaveInStartDay(String userId, String status, Date startTime, Date endTime);
    Leave getLeaveInEndDay(String userId, String status, Date startTime, Date endTime);
    List<Leave> getLeaveInDay(String userId, Date date);
    List<Leave> separationTime(Leave leave) throws ParseException;
    List<Leave> getLeaveByReceive(String receive);
}
