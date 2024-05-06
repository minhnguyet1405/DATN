package com.tth.management.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tth.common.utils.StringUtil;
import com.tth.management.model.Leave;
import com.tth.management.repository.LeaveRepository;
import com.tth.management.service.LeaveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveRepository leaveRepository;

    public static List<LocalDate> getDatesBetweenUsingJava8(
            LocalDate startDate, LocalDate endDate) {

        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());
    }

    @Override
    public List<Leave> getAllLeave(String userId, String status, Date startTime, Date endTime) {
        return leaveRepository.getAllLeave(userId, status, startTime, endTime);
    }

    @Override
    public Page<Leave> getAllLeave(Pageable pageable, List<String> userIds, String type, Date startTime, Date endTime) {
        return leaveRepository.getAllLeave(userIds, type, startTime, endTime, pageable);
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

    @Override
    public List<Leave> transform(List<Leave> leaves) throws ParseException {
        List<Leave> result = new ArrayList<>();
        for (Leave leave : leaves) {
            List<Leave> leaveList = separationTime(leave);
            result.addAll(leaveList);
        }
        return result;
    }

    @Override
    public Leave getLeaveInStartDay(String userId, String status, Date startTime, Date endTime) {
        return leaveRepository.findFirstByCreatedByAndTypeAndStartTimeBetween(userId, status, startTime,endTime);
    }

    @Override
    public Leave getLeaveInEndDay(String userId, String status, Date startTime, Date endTime) {
        return leaveRepository.findFirstByCreatedByAndTypeAndEndTimeBetween(userId, status, startTime,endTime);
    }

    @Override
    public List<Leave> getLeaveInDay(String userId, Date date) {
        return leaveRepository.findLeaveInDay(userId, date);
    }

    @Override
    public List<Leave> separationTime(Leave leave) throws ParseException {
        ObjectMapper mapper = new ObjectMapper();
        List<Leave> leaveList = new ArrayList<>();
        List<LocalDate> localDates = new ArrayList<>();
        Date startTime = leave.getStartTime();
        Date endTime = leave.getEndTime();
        LocalDate startLocal = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocal = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (startLocal.isEqual(endLocal)) {
            String time = format.format(startTime).split(" ")[0] + " 00:00:00";
            leave.setTime(format.parse(time));
            leaveList.add(leave);
        } else {
            localDates = getDatesBetweenUsingJava8(startLocal, endLocal.plusDays(1));
            for (int i = 0; i < localDates.size(); i++) {
                Date date = Date.from(localDates.get(i).atStartOfDay(ZoneId.systemDefault()).toInstant());
                Leave leaveChild = new Leave();
                leaveChild = mapper.convertValue(leave, new TypeReference<Leave>() {
                });
                leaveChild.setTime(date);
                leaveList.add(leaveChild);
            }
        }
        return leaveList;
    }

    @Override
    public List<Leave> getLeaveByReceive(String receive) {
        return leaveRepository.findByReceiveAndStartTimeAfter(receive, new Date());
    }
}
