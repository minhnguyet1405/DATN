package com.tth.id.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tth.common.message.MessageContent;
import com.tth.common.message.ResponseMessage;
import com.tth.common.utils.StringUtil;
import com.tth.id.model.Department;
import com.tth.id.model.User;
import com.tth.id.model.dto.AuthorizationResponseDTO;
import com.tth.id.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.*;

@Controller
public class DepartmentController extends BaseController {
    @Autowired
    private DepartmentService departmentService;

    public ResponseMessage getAllDepartment(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            String search = "";
            String sort = "";
            Pageable pageable;
            if (!StringUtil.isNullOrEmpty(urlParam)) {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                Integer page = params.get("page") != null ? Integer.parseInt(params.get("page")) : 0;
                Integer size = params.get("size") != null ? Integer.parseInt(params.get("size")) : 20;
                sort = !StringUtil.isNullOrEmpty(params.get("sort")) ? params.get("sort") : "name";
                search = params.get("search");
                pageable = PageRequest.of(page, size, Sort.by(sort));
            } else {
                pageable = PageRequest.of(0, 20, Sort.by("username"));
            }
            Page<Department> departments = departmentService.getAll(pageable, search.toUpperCase());
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách phòng ban",
                    new MessageContent(HttpStatus.OK.value(), "Lấy danh sách phòng ban", departments.getContent(), departments.getTotalElements()));
        }

        return response;
    }

    public ResponseMessage createDepartment(Map<String, String> headerParam,
                                            Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            String invalidData = null;
            Department department = getUserDTOFromBodyParam(bodyParam);
            Department existDepartment = departmentService.findByName(department.getName());
            if (existDepartment != null) {
                invalidData = "Đã tồn tại phòng ban với tên đăng nhập " + department.getName();
                response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                        new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
            } else {
                existDepartment = departmentService.findByPhone(department.getPhoneNumber());
                if (existDepartment != null) {
                    invalidData = "Số điện thoại đã có ở phòng ban khác " + department.getPhoneNumber();
                    response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                            new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                } else {
                    existDepartment = departmentService.findByCode(department.getCode());
                    if (existDepartment != null) {
                        invalidData = "Đã tồn tại tài khoản ứng với mã " + department.getCode();
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                    } else {
                        department.setId(UUID.randomUUID().toString());
                        department.setCreatedDate(new Date());
                        department.setIsDeleted(0);
                        departmentService.save(department);
                        response = new ResponseMessage(HttpStatus.OK.value(), "Tạo phòng ban thành công",
                                new MessageContent(HttpStatus.OK.value(), "Tạo phòng ban thành công", department));
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage updateDepartment(Map<String, String> headerParam,
                                            Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Department department = getUserDTOFromBodyParam(bodyParam);
            Department departmentExist = departmentService.findById(department.getId());
            if (departmentExist == null) {
                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm phòng ban",
                        new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy phòng ban", null));
            } else {
                String invalidData = null;
                Department existDepartment = departmentService.findByName(department.getName());
                if (existDepartment != null && !departmentExist.getId().equals(department.getId())) {
                    invalidData = "Đã tồn tại phòng ban với tên đăng nhập " + department.getName();
                    response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                            new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                } else {
                    existDepartment = departmentService.findByPhone(department.getPhoneNumber());
                    if (existDepartment != null && !departmentExist.getId().equals(department.getId())) {
                        invalidData = "Số điện thoại đã có ở phòng ban khác " + department.getPhoneNumber();
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                    } else {
                        existDepartment = departmentService.findByCode(department.getCode());
                        if (existDepartment != null && !departmentExist.getId().equals(department.getId())) {
                            invalidData = "Đã tồn tại tài khoản ứng với mã " + department.getCode();
                            response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                    new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                        } else {
                            department.setId(departmentExist.getId());
                            department.setModifiedDate(new Date());
                            department.setIsDeleted(0);
                            departmentService.save(department);
                            response = new ResponseMessage(HttpStatus.OK.value(), "Cập nhật phòng ban thành công",
                                    new MessageContent(HttpStatus.OK.value(), "Cập nhật phòng ban thành công", department));
                        }
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage deleteDepartment(Map<String, String> headerParam, Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            List<String> ids = (List<String>) bodyParam.get("ids");
            if (ids == null || ids.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "uuids không được bỏ trống hoặc không đúng định dạng",
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), "uuids không được bỏ trống hoặc không đúng định dạng", null));
            } else {
                List<Department> departments = departmentService.findByIds(ids);
                if(!CollectionUtils.isEmpty(departments)) {
                    for (Department department:departments) {
                        department.setIsDeleted(1);
                        departmentService.save(department);
                    }
                    response = new ResponseMessage(HttpStatus.OK.value(), "Xóa phòng ban thành công",
                            new MessageContent(HttpStatus.OK.value(), "Xóa phòng ban thành công", null));
                }
            }
        }
        return response;
    }

    private Department getUserDTOFromBodyParam(Map<String, Object> bodyParam) {
        String id = (String) bodyParam.get("id");
        String name = (String) bodyParam.get("name");
        String code = (String) bodyParam.get("code");
        String site = (String) bodyParam.get("site");
        Integer numberStaff = null;
        if (bodyParam.get("numberStaff") != null) {
            numberStaff = Integer.parseInt(bodyParam.get("numberStaff").toString());
        }
        String managerId = (String) bodyParam.get("managerId");
        String phoneNumber = (String) bodyParam.get("phoneNumber");
        Department department = new Department();
        if(id != null){
            department.setId(id);
        }
        department.setName(name);
        department.setCode(code);
        department.setSite(site);
        department.setNumberStaff(numberStaff);
        department.setManagerId(managerId);
        department.setPhoneNumber(phoneNumber);
        return department;
    }
}
