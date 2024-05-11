package com.tth.management.controller;

import com.tth.common.message.MessageContent;
import com.tth.common.message.ResponseMessage;
import com.tth.common.utils.StringUtil;
import com.tth.management.enums.LeaveType;
import com.tth.management.model.CheckInOut;
import com.tth.management.model.Leave;
import com.tth.management.model.dto.AuthorizationResponseDTO;
import com.tth.management.service.CheckInOutService;
import com.tth.management.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class CheckInOutController extends BaseController {
    @Value("${checkin:}")
    private Integer checkin;
    @Value("${checkout:}")
    private Integer checkout;
    @Autowired
    private CheckInOutService checkInOutService;
    @Autowired
    private LeaveService leaveService;

    public ResponseMessage getCheckInOutUser(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Pageable pageable;
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            Integer page = params.get("page") != null ? Integer.parseInt(params.get("page")) : 0;
            Integer size = params.get("size") != null ? Integer.parseInt(params.get("size")) : 20;
            String userId = params.get("userId");
            Integer month = params.get("month") != null ? Integer.parseInt(params.get("month")) : 1;
            pageable = PageRequest.of(page, size);
            Page<CheckInOut> checkInOuts = checkInOutService.findAllByUserIdAndMonth(pageable, userId, month);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công", new MessageContent(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công", checkInOuts.getContent(), checkInOuts.getTotalElements()));
        }

        return response;
    }

    private Integer getMinuteByDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter datetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateCheckIn = LocalDateTime.parse(format.format(date), datetime);
        return dateCheckIn.getHour() * 60 + dateCheckIn.getMinute();
    }

    private Integer calcularTimeLate(CheckInOut checkInOut, Leave leave) throws ParseException {
        if (checkInOut.getTimeIn() != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Integer min = getMinuteByDate(checkInOut.getTimeIn());
            if (min > checkin * 60) {
                Leave leaveInDay = null;
                if (leave != null) {
                    List<Leave> leaveList = leaveService.separationTime(leave);
                    Date now = new Date();
                    String nowString = format.format(now).split(" ")[0];
                    for (int i = 0; i < leaveList.size(); i++) {
                        if (format.format(leaveList.get(0).getTime()).split(" ")[0].equalsIgnoreCase(nowString)) {
                            leaveInDay = leaveList.get(i);
                            if (leaveInDay.getEndTime().before(checkInOut.getTimeIn())) {
                                return getMinuteByDate(checkInOut.getTimeIn()) - getMinuteByDate(leaveInDay.getEndTime());
                            }
                            if (leaveInDay.getStartTime().after(checkInOut.getTimeIn())) {
                                return min - checkin * 60;
                            }
                        }
                        return 0;
                    }
                } else {
                    return min - checkin * 60;
                }
            }
        }
        return 0;
    }

    private Integer calcularTimeSoon(CheckInOut checkInOut, Leave leave) throws ParseException {
        if (checkInOut.getTimeOut() != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Integer max = getMinuteByDate(checkInOut.getTimeOut());
            if (max < checkout * 60) {
                Leave leaveInDay = null;
                if (leave != null) {
                    List<Leave> leaveList = leaveService.separationTime(leave);
                    Date now = new Date();
                    String nowString = format.format(now).split(" ")[0];
                    for (int i = 0; i < leaveList.size(); i++) {
                        if (format.format(leaveList.get(0).getTime()).split(" ")[0].equalsIgnoreCase(nowString)) {
                            leaveInDay = leaveList.get(i);
                            if (leaveInDay.getStartTime().after(checkInOut.getTimeOut())) {
                                return getMinuteByDate(leaveInDay.getStartTime()) - getMinuteByDate(checkInOut.getTimeOut());
                            }
                            if (leaveInDay.getEndTime().before(checkInOut.getTimeOut())) {
                                return checkout * 60 - max;
                            }
                        }
                        return 0;
                    }
                } else {
                    return checkout * 60 - max;
                }
            }
        }
        return 0;
    }

    public ResponseMessage createCheckInOut(Map<String, String> headerParam, Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = (String) bodyParam.get("time");
            String userId = (String) bodyParam.get("userId");
            if (dateStr == null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Thời gian không được bỏ trống", new MessageContent(HttpStatus.BAD_REQUEST.value(), "Thời gian không được bỏ trống", null));
            } else {
                Date date = format.parse(dateStr);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
                CheckInOut checkInOutExist = checkInOutService.findFirstByUserIdAndDate(userId, dateStr.split(" ")[0]);
                Date now = new Date();
                String dateString = format.format(now);
                String startTime = dateString.split(" ")[0] + " 08:00:00";
                String endTime = dateString.split(" ")[0] + " 17:00:00";
                Leave leaveStart = leaveService.getLeaveInStartDay(userId, "LEAVE", format.parse(startTime), format.parse(endTime));
                Leave leaveEnd = leaveService.getLeaveInEndDay(userId, "LEAVE", format.parse(startTime), format.parse(endTime));
                if (checkInOutExist == null) {
                    CheckInOut checkInOut = new CheckInOut();
                    checkInOut.setId(UUID.randomUUID().toString());
                    checkInOut.setTimeIn(date);
                    checkInOut.setUpdateTime(new Date());
                    checkInOut.setUserId(userId);
                    checkInOut.setDate(dateStr.split(" ")[0]);
                    checkInOut.setMonth(dateTime.getMonthValue());
                    checkInOut.setTimeSoon(calcularTimeSoon(checkInOut, leaveEnd));
                    checkInOut.setTimeLate(calcularTimeLate(checkInOut, leaveStart));
                    checkInOutService.save(checkInOut);
                    response = new ResponseMessage(HttpStatus.OK.value(), "Check in thành công", new MessageContent(HttpStatus.OK.value(), "Check in thành công", checkInOut));
                } else {
                    checkInOutExist.setTimeOut(date);
                    checkInOutExist.setUpdateTime(new Date());
                    checkInOutExist.setTimeSoon(calcularTimeSoon(checkInOutExist, leaveEnd));
                    checkInOutExist.setTimeLate(calcularTimeLate(checkInOutExist, leaveStart));
                    checkInOutService.save(checkInOutExist);
                    response = new ResponseMessage(HttpStatus.OK.value(), "Check in thành công", new MessageContent(HttpStatus.OK.value(), "Check in thành công", checkInOutExist));
                }
            }
        }
        return response;
    }

    public ResponseMessage checkInOutHandmade(Map<String, String> headerParam, Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = (String) bodyParam.get("date");
            String userId = (String) bodyParam.get("userId");
            String startTime = (String) bodyParam.get("startTime");
            String endTime = (String) bodyParam.get("endTime");
            CheckInOut checkInOutExist = checkInOutService.findFirstByUserIdAndDate(userId, date);
            Date startDate = format.parse(date + " " + startTime + ":00");
            Date endDate = format.parse(date + " " + endTime + ":00");
            checkInOutExist.setTimeIn(startDate);
            checkInOutExist.setTimeOut(endDate);
            checkInOutService.save(checkInOutExist);
            response = new ResponseMessage(HttpStatus.OK.value(), "Check in thành công", new MessageContent(HttpStatus.OK.value(), "Check in thành công", checkInOutExist));
        }
        return response;
    }
}
