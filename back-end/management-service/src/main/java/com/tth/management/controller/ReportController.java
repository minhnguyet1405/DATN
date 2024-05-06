package com.tth.management.controller;

import com.tth.common.message.MessageContent;
import com.tth.common.message.ResponseMessage;
import com.tth.common.utils.StringUtil;
import com.tth.management.model.CheckInOut;
import com.tth.management.model.Leave;
import com.tth.management.model.dto.*;
import com.tth.management.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ReportController extends BaseController {
    @Autowired
    private ReportService reportService;

    public ResponseMessage getTimeLate(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userIds = params.get("userIds");
            List<String> userIdList = null;
            if (!StringUtil.isNullOrEmpty(userIds)) {
                userIdList = List.of(userIds.split(","));
            }
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            } catch (Exception e) {

            }
            long timeLate = reportService.countTotalTimeLate(startDate, endDate, userIdList);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lần gian đi muộn", new MessageContent(HttpStatus.OK.value(), "Lần gian đi muộn", timeLate));
        }

        return response;
    }

    public ResponseMessage getTimeSoon(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userIds = params.get("userIds");
            List<String> userIdList = null;
            if (!StringUtil.isNullOrEmpty(userIds)) {
                userIdList = List.of(userIds.split(","));
            }
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            } catch (Exception e) {

            }
            long timeLate = reportService.countTotalTimeSoon(startDate, endDate, userIdList);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lần về sớm", new MessageContent(HttpStatus.OK.value(), "Lần về sớm", timeLate));
        }

        return response;
    }

    public ResponseMessage getLeavePlan(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userIds = params.get("userIds");
            List<String> userIdList = null;
            if (!StringUtil.isNullOrEmpty(userIds)) {
                userIdList = List.of(userIds.split(","));
            }
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            } catch (Exception e) {

            }
            long timeLate = reportService.countLeavePlan(startDate, endDate, userIdList);
            response = new ResponseMessage(HttpStatus.OK.value(), "Só lượng kế hoạch nghỉ", new MessageContent(HttpStatus.OK.value(), "Só lượng kế hoạch nghỉ", timeLate));
        }

        return response;
    }

    public ResponseMessage getLeaveReal(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userIds = params.get("userIds");
            List<String> userIdList = null;
            if (!StringUtil.isNullOrEmpty(userIds)) {
                userIdList = List.of(userIds.split(","));
            }
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            } catch (Exception e) {

            }
            long leaveReal = reportService.countCheckInOutIsNull(startDate, endDate, userIdList);
            response = new ResponseMessage(HttpStatus.OK.value(), "Só lượng thực tế nghỉ", new MessageContent(HttpStatus.OK.value(), "Só lượng thực tế nghỉ", leaveReal));
        }

        return response;
    }

    public ResponseMessage getLeaveByType(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userIds = params.get("userIds");
            List<String> userIdList = null;
            if (!StringUtil.isNullOrEmpty(userIds)) {
                userIdList = List.of(userIds.split(","));
            }
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            } catch (Exception e) {

            }
            List<LeaveForTypeDTO> leaveForTypeDTOS = reportService.getLeaveForType(startDate, endDate, userIdList);
            response = new ResponseMessage(HttpStatus.OK.value(), "Báo cáo nghỉ theo loại", new MessageContent(HttpStatus.OK.value(), "Báo cáo nghỉ theo loại", leaveForTypeDTOS));
        }

        return response;
    }

    public ResponseMessage getUserTimeLateSoon(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userIds = params.get("userIds");
            List<String> userIdList = new ArrayList<>();
            if (!StringUtil.isNullOrEmpty(userIds)) {
                userIdList = List.of(userIds.split(","));
            }
            Integer month = Integer.parseInt(params.get("month"));
            List<CheckInOut> checkInOuts = reportService.getAllByMonth(month, userIdList);
            List<TopUserLateSoonDTO> topUserLateSoonDTOS = new ArrayList<>();
            Map<String, List<CheckInOut>> stringListMap = checkInOuts.stream().collect(Collectors.groupingBy(i -> i.getUserId()));
            for (Map.Entry<String, List<CheckInOut>> map : stringListMap.entrySet()) {
                TopUserLateSoonDTO topUserLateSoonDTO = new TopUserLateSoonDTO();
                topUserLateSoonDTO.setUserId(map.getKey());
                topUserLateSoonDTO.setCount(map.getValue().size());
                topUserLateSoonDTO.setDates(map.getValue().stream().map(i -> i.getDate()).collect(Collectors.toList()));
                topUserLateSoonDTOS.add(topUserLateSoonDTO);
            }
            List<TopUserLateSoonDTO> topUserLateSoonDTOSSort = new ArrayList<>();
            if(!CollectionUtils.isEmpty(topUserLateSoonDTOS)) {
                topUserLateSoonDTOSSort = topUserLateSoonDTOS.stream().sorted((a,b) -> b.getCount().compareTo(a.getCount())).collect(Collectors.toList());
            }
            response = new ResponseMessage(HttpStatus.OK.value(), "Những người nghỉ nhiều nhất", new MessageContent(HttpStatus.OK.value(), "Những người nghỉ nhiều nhất", topUserLateSoonDTOSSort));
        }
        return response;
    }

    public ResponseMessage getFrequencyLateSoon(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userIds = params.get("userIds");
            List<String> userIdList = new ArrayList<>();
            if (!StringUtil.isNullOrEmpty(userIds)) {
                userIdList = List.of(userIds.split(","));
            }
            Integer month = Integer.parseInt(params.get("month"));
            List<CheckInOut> checkInOuts = reportService.getAllByMonth(month, userIdList);
            List<FrequencyTimeLateSoon> frequencyTimeLateSoonList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(checkInOuts)) {
                List<TopUserLateSoonDTO> topUserLateSoonDTOS = new ArrayList<>();
                Map<String, List<CheckInOut>> stringListMap = checkInOuts.stream().collect(Collectors.groupingBy(i -> i.getUserId()));
                for (Map.Entry<String, List<CheckInOut>> map : stringListMap.entrySet()) {
                    TopUserLateSoonDTO topUserLateSoonDTO = new TopUserLateSoonDTO();
                    topUserLateSoonDTO.setUserId(map.getKey());
                    topUserLateSoonDTO.setCount(map.getValue().size());
                    topUserLateSoonDTOS.add(topUserLateSoonDTO);
                }
                Map<Integer, List<TopUserLateSoonDTO>> topUserLateSoonDTOSSort = topUserLateSoonDTOS.stream().collect(Collectors.groupingBy(i -> i.getCount()));
                for (Map.Entry<Integer, List<TopUserLateSoonDTO>> map : topUserLateSoonDTOSSort.entrySet()) {
                    FrequencyTimeLateSoon frequencyTimeLateSoon = new FrequencyTimeLateSoon();
                    frequencyTimeLateSoon.setFrequency(map.getKey());
                    frequencyTimeLateSoon.setCount(map.getValue().size());
                    frequencyTimeLateSoonList.add(frequencyTimeLateSoon);
                }
            }
            response = new ResponseMessage(HttpStatus.OK.value(), "Tần suất đi muộn về sớm", new MessageContent(HttpStatus.OK.value(), "Tần suất đi muộn về sớm", frequencyTimeLateSoonList));
        }
        return response;
    }

    public ResponseMessage getLeaveByTime(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userIds = params.get("userIds");
            List<String> userIdList = null;
            if (!StringUtil.isNullOrEmpty(userIds)) {
                userIdList = List.of(userIds.split(","));
            }
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            } catch (Exception e) {

            }
            List<LeaveForTimeDTO> leaveReal = reportService.getLeaveForTime(startDate, endDate, userIdList);
            response = new ResponseMessage(HttpStatus.OK.value(), "Só lượng thực tế nghỉ", new MessageContent(HttpStatus.OK.value(), "Só lượng thực tế nghỉ", leaveReal));
        }
        return response;
    }

    public ResponseMessage getLeaveByDepartment(String urlParam, Map<String, String> headerParam) throws IOException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userIds = params.get("userIds");
            List<String> userIdList = null;
            if (!StringUtil.isNullOrEmpty(userIds)) {
                userIdList = List.of(userIds.split(","));
            }
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            } catch (Exception e) {

            }
            List<LeaveForDepartment> leaveReal = reportService.getLeaveForDepartment(startDate, endDate, userIdList);
            response = new ResponseMessage(HttpStatus.OK.value(), "Só lượng thực tế nghỉ", new MessageContent(HttpStatus.OK.value(), "Só lượng thực tế nghỉ", leaveReal));
        }
        return response;
    }
}
