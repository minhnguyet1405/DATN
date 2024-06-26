package com.tth.management.scheduler;

import com.tth.management.enums.LeaveType;
import com.tth.management.model.CheckInOut;
import com.tth.management.model.Leave;
import com.tth.management.model.dto.User;
import com.tth.management.service.CheckInOutService;
import com.tth.management.service.LeaveService;
import com.tth.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Configuration
@Service
public class CheckInOutScheduler {
    @Autowired
    CheckInOutService checkInOutService;
    @Autowired
    LeaveService leaveService;

    @Autowired
    UserService userService;

//    @Scheduled(fixedDelayString = "3600000")
    @Scheduled(fixedDelayString = "60000")
    public void createDateTomorrow() throws IOException, ParseException {
        String urlParam = "page=0&size=20";
        List<User> userList = userService.getUser(urlParam);
        for (User user : userList) {
            createTomorrow(user.getUuid());
        }
    }
    public void createTomorrow(String userId) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        Date date = Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String dateString = format.format(date);
        String time = dateString.split(" ")[0];
        CheckInOut checkInOut = checkInOutService.findFirstByUserIdAndDate(userId, time);
        if(checkInOut == null) {
            List<Leave> leaves = leaveService.getLeaveInDay(userId, format.parse(dateString.split(" ")[0] + " 12:00:00"));
            CheckInOut checkInOutNew = new CheckInOut();
            checkInOutNew.setId(UUID.randomUUID().toString());
            checkInOutNew.setUpdateTime(date);
            checkInOutNew.setDate(time);
            checkInOutNew.setMonth(tomorrow.getMonthValue());
            checkInOutNew.setUserId(userId);
            checkInOutNew.setTimeLate(0);
            checkInOutNew.setTimeSoon(0);
            DayOfWeek dayOfWeek = tomorrow.getDayOfWeek();
            boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
            checkInOutNew.setWeekend(isWeekend);
            if(!CollectionUtils.isEmpty(leaves)){
                Leave leave = leaves.get(0);
                String leaveType = LeaveType.of(leave.getType()).description() + ": " + format.format(leave.getStartTime()) + " - " + format.format(leave.getEndTime());
                checkInOutNew.setNote(leaveType);
            }
            checkInOutService.save(checkInOutNew);
        }
    }
}
