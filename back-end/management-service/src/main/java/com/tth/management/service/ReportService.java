package com.tth.management.service;

import com.tth.management.model.CheckInOut;
import com.tth.management.model.dto.LeaveForDepartment;
import com.tth.management.model.dto.LeaveForTimeDTO;
import com.tth.management.model.dto.LeaveForTypeDTO;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ReportService {
    Long countTotalTimeLate(Date startTime, Date endTime, List<String> userIds);
    Long countTotalTimeSoon(Date startTime, Date endTime, List<String> userIds);
    Long countLeavePlan(Date startTime, Date endTime, List<String> userIds);
    Long countCheckInOutIsNull(Date startTime, Date endTime, List<String> userIds);
    List<LeaveForTimeDTO> getLeaveForTime(Date startTime, Date endTime, List<String> userIds);
    List<LeaveForDepartment> getLeaveForDepartment(Date startTime, Date endTime, List<String> userIds) throws IOException;
    List<LeaveForTypeDTO> getLeaveForType(Date startTime, Date endTime, List<String> userIds);
    List<CheckInOut> getAllByMonth(Integer month, List<String> userIds);
}
