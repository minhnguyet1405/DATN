package com.tth.management.service.impl;

import com.tth.management.model.CheckInOut;
import com.tth.management.model.Leave;
import com.tth.management.model.dto.LeaveForDepartment;
import com.tth.management.model.dto.LeaveForTimeDTO;
import com.tth.management.model.dto.LeaveForTypeDTO;
import com.tth.management.model.dto.User;
import com.tth.management.repository.CheckInOutRepository;
import com.tth.management.repository.LeaveRepository;
import com.tth.management.service.ReportService;
import com.tth.management.service.UserService;
import io.swagger.models.auth.In;
import org.apache.lucene.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private CheckInOutRepository checkInOutRepository;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private UserService userService;

    @Override
    public Long countTotalTimeLate(Date startTime, Date endTime, List<String> userIds) {
        if (!CollectionUtils.isEmpty(userIds)) {
            return checkInOutRepository.countTimeLate(userIds, startTime, endTime);
        } else {
            return checkInOutRepository.countTimeLate(startTime, endTime);
        }
    }

    @Override
    public Long countTotalTimeSoon(Date startTime, Date endTime, List<String> userIds) {
        if (!CollectionUtils.isEmpty(userIds)) {
            return checkInOutRepository.countTimeSoon(userIds, startTime, endTime);
        } else {
            return checkInOutRepository.countTimeSoon(startTime, endTime);
        }
    }

    @Override
    public Long countLeavePlan(Date startTime, Date endTime, List<String> userIds) {
        if (!CollectionUtils.isEmpty(userIds)) {
            return leaveRepository.countLeavePlan(userIds, startTime, endTime);
        } else {
            return leaveRepository.countLeavePlan(startTime, endTime);
        }
    }

    @Override
    public Long countCheckInOutIsNull(Date startTime, Date endTime, List<String> userIds) {
        if (!CollectionUtils.isEmpty(userIds)) {
            return checkInOutRepository.sumCheckNull(userIds, startTime, endTime);
        } else {
            return checkInOutRepository.sumCheckNull(startTime, endTime);
        }
    }

    @Override
    public List<LeaveForTimeDTO> getLeaveForTime(Date startTime, Date endTime, List<String> userIds) {
        List<CheckInOut> checkInOuts = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userIds)) {
            checkInOuts = checkInOutRepository.getLeaveReal(userIds, startTime, endTime);
        } else {
            checkInOuts = checkInOutRepository.getLeaveReal(startTime, endTime);
        }
        List<LeaveForTimeDTO> leaveForTimeDTOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(checkInOuts)){
            Map<String, List<CheckInOut>> stringListMap = checkInOuts.stream().collect(Collectors.groupingBy(i -> i.getDate()));
            for (Map.Entry<String, List<CheckInOut>> map : stringListMap.entrySet()) {
                LeaveForTimeDTO leave = new LeaveForTimeDTO();
                leave.setDay(map.getKey());
                leave.setNumber(map.getValue().size());
                leaveForTimeDTOS.add(leave);
            }
        }
        return leaveForTimeDTOS;
    }

    @Override
    public List<LeaveForDepartment> getLeaveForDepartment(Date startTime, Date endTime, List<String> userIds) throws IOException {
        String urlParam = "page=0&size=20";
        List<User> userList = userService.getUser(urlParam);
        List<CheckInOut> checkInOuts = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userIds)) {
            checkInOuts = checkInOutRepository.getLeaveReal(userIds, startTime, endTime);
        } else {
            checkInOuts = checkInOutRepository.getLeaveReal(startTime, endTime);
        }
        List<LeaveForDepartment> leaveForTimeDTOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(checkInOuts)){
            String departmentId = null;
            for (CheckInOut checkInOut : checkInOuts) {
                List<User> users = userList.stream().filter(i -> i.getUuid().equalsIgnoreCase(checkInOut.getUserId())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(users)){
                    checkInOut.setDepartmentId(users.get(0).getDepartment().getName());
                }
            }
            Map<String, List<CheckInOut>> stringListMap = checkInOuts.stream().collect(Collectors.groupingBy(i -> i.getDepartmentId()));
            for (Map.Entry<String, List<CheckInOut>> map : stringListMap.entrySet()) {
                LeaveForDepartment leave = new LeaveForDepartment();
                leave.setDepartment(map.getKey());
                leave.setNumber(map.getValue().size());
                leaveForTimeDTOS.add(leave);
            }
        }
        return leaveForTimeDTOS;
    }

    @Override
    public List<LeaveForTypeDTO> getLeaveForType(Date startTime, Date endTime, List<String> userIds) {
        Integer page = 0;
        Integer size = 100;
        Pageable pageable;
        pageable = PageRequest.of(page, size);
        Map<String, List<Leave>> stringListMap = new HashMap<>();
        Integer different = 0;
        List<Leave> leaveList = leaveRepository.getAllLeave(userIds, null, startTime, endTime,pageable).getContent();
        long relaLeave = 0;
        if(userIds != null){
            relaLeave = checkInOutRepository.sumCheckNull(userIds,startTime,endTime);
        }else {
            relaLeave = checkInOutRepository.sumCheckNull(startTime,endTime);
        }
        if(relaLeave > leaveList.size()) {
            different = Integer.parseInt(String.valueOf(relaLeave)) - leaveList.size();
        }
        List<LeaveForTypeDTO> leaveForTypeDTOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(leaveList)) {
            stringListMap = leaveList.stream().collect(Collectors.groupingBy(p -> p.getType()));
            for (Map.Entry<String, List<Leave>> entry : stringListMap.entrySet()) {
                LeaveForTypeDTO leaveForTypeDTO = new LeaveForTypeDTO();
                leaveForTypeDTO.setType(entry.getKey());
                leaveForTypeDTO.setCount(entry.getValue().size());
                leaveForTypeDTOS.add(leaveForTypeDTO);
            }
        }
        if(different > 0) {
            LeaveForTypeDTO leaveForTypeDTO = new LeaveForTypeDTO("ABSENT", different);
            leaveForTypeDTOS.add(leaveForTypeDTO);
        }
        return leaveForTypeDTOS;
    }

    @Override
    public List<CheckInOut> getAllByMonth(Integer month, List<String> userIds) {
        if(!CollectionUtils.isEmpty(userIds)) {
            return checkInOutRepository.findByMonthAndUserIdInAndWeekend(month, userIds, false);
        }
        return checkInOutRepository.findByMonthAndWeekend(month, false);
    }
}
