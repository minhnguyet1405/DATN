package com.tth.management.controller;

import com.tth.common.message.MessageContent;
import com.tth.common.message.ResponseMessage;
import com.tth.common.utils.StringUtil;
import com.tth.management.model.CheckInOut;
import com.tth.management.model.dto.AuthorizationResponseDTO;
import com.tth.management.service.CheckInOutService;
import io.swagger.models.auth.In;
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

    public ResponseMessage getAllDepartment(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            String sort = "";
            Pageable pageable;
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            Integer page = params.get("page") != null ? Integer.parseInt(params.get("page")) : 0;
            Integer size = params.get("size") != null ? Integer.parseInt(params.get("size")) : 20;
            String userId = params.get("userId");
            Integer month = params.get("month") != null ? Integer.parseInt(params.get("month")) : 1;
            pageable = PageRequest.of(page, size);

            Page<CheckInOut> departments = checkInOutService.findAllByUserIdAndMonth(pageable, userId, month);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công",
                    new MessageContent(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công", departments.getContent(), departments.getTotalElements()));
        }

        return response;
    }

    public ResponseMessage createCheckInOut(Map<String, String> headerParam,
                                            Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = (String) bodyParam.get("time");
            String userId = (String) bodyParam.get("userId");
            if (dateStr == null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Thời gian không được bỏ trống",
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), "Thời gian không được bỏ trống", null));
            } else {
                Date date = format.parse(dateStr);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
                Integer min = dateTime.getHour() * 60 + dateTime.getMinute();
                CheckInOut checkInOutExist = checkInOutService.findFirstByUserIdAndDate(userId, dateStr.split(" ")[0]);
                if (checkInOutExist == null) {
                    CheckInOut checkInOut = new CheckInOut();
                    checkInOut.setId(UUID.randomUUID().toString());
                    checkInOut.setTimeIn(date);
                    checkInOut.setUpdateTime(new Date());
                    checkInOut.setUserId(userId);
                    checkInOut.setDate(dateStr.split(" ")[0]);
                    checkInOut.setMonth(dateTime.getMonthValue());
                    if(min > checkin*60) {
                        checkInOut.setTimeLate(min - checkin*60);
                    }else {
                        checkInOut.setTimeLate(0);
                    }

                    if(min < checkout*60) {
                        checkInOut.setTimeLate(checkout*60 - min);
                    }else {
                        checkInOut.setTimeLate(0);
                    }

                    checkInOutService.save(checkInOut);
                    response = new ResponseMessage(HttpStatus.OK.value(), "Check in thành công",
                            new MessageContent(HttpStatus.OK.value(), "Check in thành công", checkInOut));
                } else {
                    if(min < checkout*60) {
                        checkInOutExist.setTimeSoon(checkout*60 - min);
                    }else {
                        checkInOutExist.setTimeSoon(0);
                    }
                    checkInOutExist.setTimeOut(date);
                    checkInOutExist.setUpdateTime(new Date());
                    checkInOutService.save(checkInOutExist);
                    response = new ResponseMessage(HttpStatus.OK.value(), "Check in thành công",
                            new MessageContent(HttpStatus.OK.value(), "Check in thành công", checkInOutExist));
                }
            }
        }
        return response;
    }
}
