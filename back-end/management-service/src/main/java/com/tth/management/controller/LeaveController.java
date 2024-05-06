package com.tth.management.controller;

import com.tth.common.message.MessageContent;
import com.tth.common.message.ResponseMessage;
import com.tth.common.utils.StringUtil;
import com.tth.management.model.Leave;
import com.tth.management.model.dto.AuthorizationResponseDTO;
import com.tth.management.model.dto.User;
import com.tth.management.service.EmailService;
import com.tth.management.service.LeaveService;
import com.tth.management.service.UserService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class LeaveController extends BaseController {
    @Autowired
    private LeaveService leaveService;
    @Autowired
    EmailService emailService;
    @Autowired
    UserService userService;

    public ResponseMessage getLeaveUser(String urlParam, Map<String, String> headerParam) throws ParseException {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userId = params.get("userId");
            String status = params.get("status");
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            } catch (Exception e) {
                return new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Thời gian không đúng định dạng",
                        new MessageContent(HttpStatus.FORBIDDEN.value(), "Thời gian không đúng định dạng", null));
            }
            List<Leave> leaves = leaveService.getAllLeave(userId, status, startDate, endDate);
            List<Leave> result = leaveService.transform(leaves);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công",
                    new MessageContent(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công", result));
        }
        return response;
    }

    public ResponseMessage getAllLeave(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Pageable pageable;
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            Integer page = params.get("page") != null ? Integer.parseInt(params.get("page")) : 0;
            Integer size = params.get("size") != null ? Integer.parseInt(params.get("size")) : 20;
            String userId = params.get("userId");
            List<String> userIds = new ArrayList<>();
            if(!StringUtil.isNullOrEmpty(userId)) {
                userIds = List.of(userId.split(","));
            }
            String type = params.get("type");
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            } catch (Exception e) {

            }
            pageable = PageRequest.of(page, size);
            Page<Leave> allLeave = leaveService.getAllLeave(pageable, userIds, type, startDate, endDate);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công",
                    new MessageContent(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công", allLeave.getContent(), allLeave.getTotalElements()));
        }

        return response;
    }

    public ResponseMessage createLeave(Map<String, String> headerParam,
                                       Map<String, Object> bodyParam) throws ParseException, IOException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Leave leave = getUserDTOFromBodyParam(bodyParam, dto);
            leave.setCreatedDate(new Date());
            leave.setModifiedDate(new Date());
            leave.setIsAccept(0);
            leaveService.save(leave);
            response = new ResponseMessage(HttpStatus.OK.value(), "Đăng ký nghỉ thành công",
                    new MessageContent(HttpStatus.OK.value(), "Đăng ký nghỉ thành công", leave));
            Map<String, Object> bodyParamSendEmail = new HashMap<>();
            try {
                String urlParam = "page=0&size=20";
                List<User> users = userService.getUser(urlParam);
                User user = users.stream().filter(i -> i.getUuid().equalsIgnoreCase(leave.getReceive())).collect(Collectors.toList()).get(0);
                bodyParamSendEmail.put("email", user.getEmail());
                bodyParamSendEmail.put("name", dto.getUsername());
                bodyParamSendEmail.put("uuid", user.getUuid());
                emailService.sendEmail(headerParam, bodyParamSendEmail);
            }catch (Exception e){
                response = new ResponseMessage(HttpStatus.OK.value(), "Lỗi gửi Email",
                        new MessageContent(HttpStatus.OK.value(), "Lỗi gửi Email", null));
            }
        }
        return response;
    }

    public ResponseMessage updateLeave(Map<String, String> headerParam,
                                       Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Leave leave = getUserDTOFromBodyParam(bodyParam, dto);
            Leave leaveExist = leaveService.findById(leave.getId());
            if (leaveExist == null) {
                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy bản ghi",
                        new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy bản ghi", null));
            } else {
                leave.setCreatedDate(leaveExist.getCreatedDate());
                leave.setModifiedDate(new Date());
                leaveService.save(leave);
                response = new ResponseMessage(HttpStatus.OK.value(), "Đăng ký nghỉ thành công",
                        new MessageContent(HttpStatus.OK.value(), "Đăng ký nghỉ thành công", leave));
            }
        }
        return response;
    }

    private Leave getUserDTOFromBodyParam(Map<String, Object> bodyParam, AuthorizationResponseDTO dto) {
        String id = (String) bodyParam.get("id");
        String type = (String) bodyParam.get("type");
        String reason = (String) bodyParam.get("reason");
        String startTime = (String) bodyParam.get("startTime");
        String endTime = (String) bodyParam.get("endTime");
        String receive = (String) bodyParam.get("receive");
        Date startDate = null;
        Date endDate = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startDate = format.parse(startTime);
            endDate = format.parse(endTime);
        } catch (Exception e) {
        }
        Leave leave = new Leave();
        leave.setId(id != null ? id : UUID.randomUUID().toString());
        leave.setType(type);
        leave.setReason(reason);
        leave.setStartTime(startDate);
        leave.setEndTime(endDate);
        leave.setReceive(receive);
        leave.setCreatedBy(dto.getUuid());
        return leave;
    }

    public ResponseMessage deleteLeave(Map<String, String> headerParam, String pathParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            if (StringUtil.isNullOrEmpty(pathParam)) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "uuids không được bỏ trống hoặc không đúng định dạng",
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), "uuids không được bỏ trống hoặc không đúng định dạng", null));
            } else {
                Leave leaves = leaveService.findById(pathParam);
                leaveService.delete(leaves);
                response = new ResponseMessage(HttpStatus.OK.value(), "Xóa lịch thành công",
                        new MessageContent(HttpStatus.OK.value(), "Xóa lịch thành công", null));
            }
        }
        return response;
    }

    public ResponseMessage getListApprove(String pathParam, Map<String, String> headerParam) throws ParseException {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            List<Leave> leaves = leaveService.getLeaveByReceive(dto.getUuid());
            response = new ResponseMessage(HttpStatus.OK.value(), "Danh sách duyệt đơn",
                    new MessageContent(HttpStatus.OK.value(), "Danh sách duyệt đơn", leaves));
        }
        return response;
    }

    public ResponseMessage approveLeave(Map<String, String> headerParam,
                                        Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            String leaveId = (String) bodyParam.get("leaveId");
            Integer status = Integer.parseInt(bodyParam.get("status").toString());
            Leave leave = leaveService.findById(leaveId);
            leave.setIsAccept(status);
            leaveService.save(leave);
            if(status == 1) {
                response = new ResponseMessage(HttpStatus.OK.value(), "Phê duyệt thành công",
                        new MessageContent(HttpStatus.OK.value(), "Phê duyệt thành công", leave));
            }else{
                response = new ResponseMessage(HttpStatus.OK.value(), "Hủy phê duyệt thành công",
                        new MessageContent(HttpStatus.OK.value(), "Hủy phê duyệt thành công", leave));
            }

        }
        return response;
    }
}
