package com.tth.id.controller;

import com.tth.common.message.MessageContent;
import com.tth.common.message.ResponseMessage;
import com.tth.common.utils.StringUtil;
import com.tth.id.model.Department;
import com.tth.id.model.User;
import com.tth.id.model.dto.AuthorizationResponseDTO;
import com.tth.id.model.dto.UserDTO;
import com.tth.id.model.dto.UserResponse;
import com.tth.id.service.DepartmentService;
import com.tth.id.service.UserService;
import com.tth.id.validation.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tth.id.service.impl.UserServiceImpl.transformUserResponse;

@Controller
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    public ResponseMessage getAllUsers(String requestUrl, String method, String urlParam, Map<String, String> headerParam) {
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
                sort = !StringUtil.isNullOrEmpty(params.get("sort")) ? params.get("sort") : "username";
                search = params.get("search");
                pageable = PageRequest.of(page, size, Sort.by(sort));
            } else {
                pageable = PageRequest.of(0, 20, Sort.by("username"));
            }
            Page<UserResponse> userResponsePage = userService.getAll(pageable, search);
            if(userResponsePage != null){
                response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách người dùng",
                        new MessageContent(HttpStatus.OK.value(), "Lấy danh sách người dùng", userResponsePage.getContent(), userResponsePage.getTotalElements()));
            }else{
                response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách người dùng",
                        new MessageContent(HttpStatus.OK.value(), "Lấy danh sách người dùng", null));
            }
        }

        return response;
    }

    public ResponseMessage getUserInterval() {
        ResponseMessage response = null;
        String search = "";
        Pageable pageable;
        pageable = PageRequest.of(0, 100, Sort.by("username"));
        Page<UserResponse> userResponsePage = userService.getAll(pageable, search);
        response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách người dùng",
                new MessageContent(HttpStatus.OK.value(), "Lấy danh sách người dùng", userResponsePage.getContent(), userResponsePage.getTotalElements()));
        return response;
    }

    public ResponseMessage getUserByIdInterval(String pathParam) {
        ResponseMessage response = null;
        User user = userService.findByUuid(pathParam);
        response = new ResponseMessage(HttpStatus.OK.value(), "Lấy người dùng theo uuid",
                new MessageContent(HttpStatus.OK.value(), "Lấy người dùng theo uuid", user));
        return response;
    }

    public ResponseMessage getAllUserByDepartment(String urlParam, Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
                Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
                String department = params.get("department");

            List<User> userList = userService.findByDepartment(department);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách phòng ban",
                    new MessageContent(HttpStatus.OK.value(), "Lấy danh sách phòng ban", userList));
        }

        return response;
    }

    public ResponseMessage getAllUserByManager(Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            List<User> userList = userService.getUserByManagement(dto.getUuid());
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách nhân viên",
                    new MessageContent(HttpStatus.OK.value(), "Lấy danh sách nhân viên", userList));
        }

        return response;
    }

    public ResponseMessage getManagerByUser(Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Department department = departmentService.findById(dto.getDepartment());
            List<User> userList = new ArrayList<>();
            User management = userService.findByUuid(department.getManagerId());
            userList.add(management);
            response = new ResponseMessage(HttpStatus.OK.value(), "Lấy danh sách quản lý",
                    new MessageContent(HttpStatus.OK.value(), "Lấy danh sách quản lý", userList));
        }

        return response;
    }

    public ResponseMessage createUser(String requestUrl, String method, Map<String, String> headerParam,
                                      Map<String, Object> bodyParam) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            UserDTO userDTO = getUserDTOFromBodyParam(bodyParam);
            String invalidData = new UserValidation().validateCreate(userDTO);
            if (invalidData != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                //check username, phoneNumber, email exist????
                User existUser = userService.findByUsername(userDTO.getUsername());
                if (existUser != null) {
                    invalidData = "Đã tồn tại tài khoản ứng với tên đăng nhập " + userDTO.getUsername();
                    response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                            new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                } else {
                    existUser = userService.findByPhoneNumber(userDTO.getPhoneNumber());
                    if (existUser != null) {
                        invalidData = "Đã tồn tại tài khoản ứng với số điện thoại " + userDTO.getPhoneNumber();
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                    } else {
                        existUser = userService.findByEmail(userDTO.getEmail());
                        if (existUser != null) {
                            invalidData = "Đã tồn tại tài khoản ứng với email " + userDTO.getEmail();
                            response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                    new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                        } else {
                            response = new ResponseMessage(HttpStatus.OK.value(), "Tạo tài khoản thành công",
                                    new MessageContent(HttpStatus.OK.value(), "Tạo tài khoản thành công", userService.save(userDTO, dto.getUuid())));
                        }
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage updateUser(Map<String, Object> bodyParam, Map<String, String> headerParam,
                                      String pathParam, String method, String requestPath) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            User user = userService.findByUuid(pathParam);
            if (user == null) {
                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng",
                        new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng", null));
            } else {
                UserDTO userDTO = getUserDTOFromBodyParam(bodyParam);
                String invalidData = new UserValidation().validateUser(userDTO);
                if (invalidData != null) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                } else {
                    //check phoneNumber, email exist????
                    User existUser = userService.findByPhoneNumber(userDTO.getPhoneNumber());
                    if (existUser != null && !existUser.getUuid().equals(pathParam)) {
                        invalidData = "Đã tồn tại tài khoản ứng với số điện thoại " + userDTO.getPhoneNumber();
                        response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                    } else {
                        existUser = userService.findByEmail(userDTO.getEmail());
                        if (existUser != null && !existUser.getUuid().equals(pathParam)) {
                            invalidData = "Đã tồn tại tài khoản ứng với email " + userDTO.getEmail();
                            response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                    new MessageContent(HttpStatus.CONFLICT.value(), invalidData, null));
                        } else {
                            response = new ResponseMessage(HttpStatus.OK.value(), "Cập nhật tài khoản thành công",
                                    new MessageContent(HttpStatus.OK.value(), "Cập nhật tài khoản thành công", userService.update(pathParam, user, userDTO, dto.getUuid())));
                        }
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage changePassword(Map<String, Object> bodyParam, Map<String, String> headerParam,
                                          String pathParam, String method, String requestPath) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            String curPass = (String) bodyParam.get("currentPassword");
            String newPass = (String) bodyParam.get("newPassword");
            String matchingPass = (String) bodyParam.get("matchingPassword");
            String invalidData = new UserValidation().validateChangePassword(curPass, newPass, matchingPass);
            if (invalidData != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                User user = userService.findByUuid(dto.getUuid());
                if (user == null) {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng",
                            new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng", null));
                } else {
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    if (encoder.matches(curPass, user.getPassword())) {
                        response = new ResponseMessage(HttpStatus.OK.value(), "Cập nhật mật khẩu thành công",
                                new MessageContent(HttpStatus.OK.value(), "Cập nhật mật khẩu thành công", userService.changePassword(user, newPass)));
                    } else {
                        response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Mật khẩu hiện tại không khớp",
                                new MessageContent(HttpStatus.FORBIDDEN.value(), "Mật khẩu hiện tại không khớp", null));
                    }
                }
            }
        }
        return response;
    }

    public ResponseMessage deleteUser(String requestPath, Map<String, String> headerParam, Map<String, Object> bodyParam,
                                      String method) throws ParseException {
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);

        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            List<String> uuids = (List<String>) bodyParam.get("uuids");
            if (uuids == null || uuids.isEmpty()) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "uuids không được bỏ trống hoặc không đúng định dạng",
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), "uuids không được bỏ trống hoặc không đúng định dạng", null));
            } else {
                List<User> userList = userService.findByUuidIn(uuids);
                if (userList != null && !userList.isEmpty()) {
                    //ko dc xoa admin va chinh minh
                    List<User> userNotDelete = new ArrayList<>();
                    userList.stream().filter((user) -> ("admin".equalsIgnoreCase(user.getUsername()) || dto.getUuid().equals(user.getUuid())))
                            .forEachOrdered((user) -> {
                        userNotDelete.add(user);
                    });
                    userList.removeAll(userNotDelete);
                    if(userList != null && !userList.isEmpty()) {
                        userService.deleteMultiUser(userList);
                        response = new ResponseMessage(HttpStatus.OK.value(), "Xóa tài khoản thành công",
                                new MessageContent(HttpStatus.OK.value(), "Xóa tài khoản thành công", null));
                    } else {
                        response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Không được xóa admin hoặc chính mình",
                                new MessageContent(HttpStatus.FORBIDDEN.value(), "Không được xóa admin hoặc chính mình", null));
                    }
                } else {
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng",
                            new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng", null));
                }
            }
        }
        return response;
    }

    private UserDTO getUserDTOFromBodyParam(Map<String, Object> bodyParam) {
        String username = (String) bodyParam.get("username");
        String password = (String) bodyParam.get("password");
        String matchingPassword = (String) bodyParam.get("matchingPassword");
        String fullName = (String) bodyParam.get("fullName");
        String phoneNumber = (String) bodyParam.get("phoneNumber");
        String email = (String) bodyParam.get("email");
        Integer gender = (Integer) bodyParam.get("gender");
        String birthday = (String) bodyParam.get("birthday");
        String address = (String) bodyParam.get("address");
        String avatar = (String) bodyParam.get("avatar");
        Integer role = (Integer) bodyParam.get("role");
        String department = (String) bodyParam.get("department");
        String position = (String) bodyParam.get("position");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setPassword(password);
        userDTO.setMatchingPassword(matchingPassword);
        userDTO.setFullName(fullName);
        userDTO.setPhoneNumber(phoneNumber);
        userDTO.setEmail(email);
        userDTO.setGender(gender);
        userDTO.setBirthday(birthday);
        userDTO.setAddress(address);
        userDTO.setAvatar(avatar);
        userDTO.setRole(role);
        userDTO.setDepartment(department);
        userDTO.setPosition(position);
        return userDTO;
    }

    public ResponseMessage findUserByUuid(String requestUrl, String method, String urlParam, Map<String, String> headerParam, String pathParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            User user = userService.findByUuid(pathParam);
            if (user == null) {
                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng",
                        new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng", null));
            } else {
                response = new ResponseMessage(HttpStatus.OK.value(), "Lấy chi tiết người dùng",
                        new MessageContent(HttpStatus.OK.value(), "Lấy chi tiết người dùng", transformUserResponse(user)));
            }
        }
        return response;
    }

}
