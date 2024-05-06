package com.tth.management.controller;

import com.tth.common.message.MessageContent;
import com.tth.common.message.ResponseMessage;
import com.tth.common.utils.StringUtil;
import com.tth.management.model.NumberLeave;
import com.tth.management.model.dto.AuthorizationResponseDTO;
import com.tth.management.service.NumberLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class NumberLeaveController extends BaseController {
    @Autowired
    private NumberLeaveService numberLeaveService;

    public ResponseMessage getNumberLeaveByUser(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String userId = params.get("userId");
            NumberLeave numberLeave = numberLeaveService.findByUserId(userId);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy số phép của nhân viên thành công",
                    new MessageContent(HttpStatus.OK.value(), "Lấy số phép của nhân viên thành công", numberLeave));
        }
        return response;
    }
}
