package com.tth.management.controller;

import com.tth.common.message.MessageContent;
import com.tth.common.message.ResponseMessage;
import com.tth.common.utils.StringUtil;
import com.tth.management.model.CheckInOut;
import com.tth.management.model.Leave;
import com.tth.management.model.dto.AuthorizationResponseDTO;
import com.tth.management.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class LeaveController extends BaseController {
    @Autowired
    private LeaveService leaveService;

    public ResponseMessage getAllLeave(String urlParam, Map<String, String> headerParam) {
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
            String status = params.get("status");
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");
            Date startDate = null;
            Date endDate = null;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startDate = format.parse(startTime);
                endDate = format.parse(endTime);
            }catch (Exception e){
                return new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Thời gian không đúng định dạng",
                        new MessageContent(HttpStatus.FORBIDDEN.value(), "Thời gian không đúng định dạng", null));
            }
            pageable = PageRequest.of(page, size);
            Page<Leave> departments = leaveService.getAllLeave(pageable, userId, status, startDate, endDate);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công",
                    new MessageContent(HttpStatus.OK.value(), "Lấy danh sách dữ liệu chấm công", departments.getContent(), departments.getTotalElements()));
        }
        return response;
    }

    public ResponseMessage createLeave(Map<String, String> headerParam,
                                            Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Leave leave = getUserDTOFromBodyParam(bodyParam, dto);
            leave.setCreatedDate(new Date());
            leave.setModifiedDate(new Date());
            leaveService.save(leave);
            response = new ResponseMessage(HttpStatus.OK.value(), "Đăng ký nghỉ thành công",
                    new MessageContent(HttpStatus.OK.value(), "Đăng ký nghỉ thành công", leave));
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
            if(leaveExist == null){
                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy bản ghi",
                        new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy bản ghi", null));
            }else{
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
        String status = (String) bodyParam.get("status");
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
        }catch (Exception e){
        }
        Leave leave = new Leave();
        leave.setId(id != null ? id : UUID.randomUUID().toString());
        leave.setStatus(status);
        leave.setReason(reason);
        leave.setStartTime(startDate);
        leave.setEndTime(endDate);
        leave.setReceive(receive);
        leave.setCreatedBy(dto.getUuid());
        return leave;
    }

    public ResponseMessage deleteLeave(Map<String, String> headerParam, Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            List<String> ids = (List<String>) bodyParam.get("ids");
            if (ids == null || ids.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "uuids không được bỏ trống hoặc không đúng định dạng",
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), "uuids không được bỏ trống hoặc không đúng định dạng", null));
            } else {
                List<Leave> leaves = leaveService.findByIdIn(ids);
                if(!CollectionUtils.isEmpty(leaves)) {
                    for (Leave leave:leaves) {
                        leaveService.delete(leave);
                    }
                    response = new ResponseMessage(HttpStatus.OK.value(), "Xóa lịch thành công",
                            new MessageContent(HttpStatus.OK.value(), "Xóa lịch thành công", null));
                }
            }
        }
        return response;
    }
}
