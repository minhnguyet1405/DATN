package com.tth.id.service.impl;

import com.tth.common.utils.StringUtil;
import com.tth.id.model.Department;
import com.tth.id.model.User;
import com.tth.id.model.dto.UserDTO;
import com.tth.id.model.dto.UserResponse;
import com.tth.id.repository.DepartmentRepository;
import com.tth.id.repository.UserRepository;
import com.tth.id.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Page<UserResponse> getAll(Pageable pageable, String search) {
        Page<User> userList;
        if(StringUtil.isNullOrEmpty(search)){
            userList = userRepository.findAllByStatus(pageable, 1);
        } else {
            userList = userRepository.findByKeyword(search.toUpperCase(), search.toUpperCase(), search.toUpperCase(), 1, pageable);
        }
        return transform(userList);
    }

    @Override
    public UserResponse findById(String id) {
        return null;
    }

    private Page<UserResponse> transform(Page<User> userList) {
        Page<UserResponse> userResponsePage = null;
        if(userList.getTotalElements() > 0) {
            userResponsePage = userList.map(user -> {
                UserResponse userResponse = new UserResponse();
                userResponse.setUuid(user.getUuid());
                userResponse.setAddress(user.getAddress());
                userResponse.setAvatar(user.getAvatar());
                userResponse.setBirthday(user.getBirthday());
                userResponse.setEmail(user.getEmail());
                userResponse.setUsername(user.getUsername());
                userResponse.setGender(user.getGender());
                userResponse.setFullName(user.getFullName());
                userResponse.setPhoneNumber(user.getPhoneNumber());
                userResponse.setRole(user.getRole());
                userResponse.setPosition(user.getPosition());
                if(!StringUtil.isNullOrEmpty(user.getDepartment())){
                    Department department = departmentRepository.findById(user.getDepartment()).orElse(null);
                    userResponse.setDepartment(department);
                }
                return userResponse;
            });
        }
        return userResponsePage;
    }

    @Override
    public UserResponse save(UserDTO userDTO, String uuid) throws ParseException {
        User user = transform(userDTO);
        user.setUuid(UUID.randomUUID().toString());
        user.setCreatedDate(new Date());
        user.setCreatedBy(uuid);
        user.setStatus(1);
        User newUser = userRepository.save(user);
        return transformUserResponse(newUser);
    }

    public static UserResponse transformUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setFullName(user.getFullName());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setEmail(user.getEmail());
        userResponse.setGender(user.getGender());
        userResponse.setBirthday(user.getBirthday());
        userResponse.setAddress(user.getAddress());
        userResponse.setAvatar(user.getAvatar());
        userResponse.setRole(user.getRole());
        userResponse.setPosition(user.getPosition());
        return userResponse;
    }

    private User transform(UserDTO userDTO) throws ParseException {
        User user = new User();
        if(!StringUtil.isNullOrEmpty(userDTO.getUsername())){
            user.setUsername(userDTO.getUsername());
        }
        if(!StringUtil.isNullOrEmpty(userDTO.getPassword())){
            user.setPassword(encoder.encode(userDTO.getPassword()));
        }
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        user.setBirthday(format.parse(userDTO.getBirthday()));
        user.setAddress(userDTO.getAddress());
        user.setAvatar(userDTO.getAvatar());
        user.setRole(userDTO.getRole());
        user.setPosition(userDTO.getPosition());
        if(!StringUtil.isNullOrEmpty(userDTO.getAvatar())){
            user.setAvatar("http://localhost:8683/v1.0/upload/user/avatar/21042024/4221d1bf-22f7-47e6-bd7e-006d3abf81ce.png");
        }
        return user;
    }

    @Override
    public UserResponse update(String id, User user, UserDTO userDTO, String uuid) throws ParseException {
        user.setModifiedBy(uuid);
        user.setModifiedDate(new Date());
        if(!StringUtil.isNullOrEmpty(userDTO.getPassword())){
            user.setPassword(encoder.encode(userDTO.getPassword()));
        }
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(userDTO.getRole());
        user.setDepartment(userDTO.getDepartment());
        user.setAvatar(userDTO.getAvatar());
        user.setAddress(userDTO.getAddress());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        user.setBirthday(format.parse(userDTO.getBirthday()));
        user.setGender(userDTO.getGender());
        user.setPosition(userDTO.getPosition());
        user.setStatus(1);
        if(StringUtil.isNullOrEmpty(userDTO.getAvatar())){
            user.setAvatar("http://localhost:8683/v1.0/upload/user/avatar/21042024/4221d1bf-22f7-47e6-bd7e-006d3abf81ce.png");
        }
        User newUser = userRepository.save(user);
        return transformUserResponse(newUser);
    }

    @Override
    public void deleteMultiUser(List<User> userList) {
        for (User user : userList) {
            user.setStatus(0);
        }
        userRepository.saveAll(userList);
    }

    @Override
    public User findByUuid(String id) {
        Optional<User> user = userRepository.findByUuidAndStatus(id, 1);
        if(user.isPresent()){
            return user.get();
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsernameAndStatus(username, 1);
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumberAndStatus(phoneNumber, 1);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, 1);
    }

    @Override
    public UserResponse changePassword(User user, String newPassword) {
        user.setPassword(encoder.encode(newPassword));
        User newUser = userRepository.save(user);
        return transformUserResponse(newUser);
    }

    @Override
    public List<User> findByUuidIn(List<String> uuids) {
        return userRepository.findByUuidInAndStatus(uuids, 1);
    }

    @Override
    public List<User> findByDepartment(String department) {
        return userRepository.findByDepartmentAndStatus(department, 1);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> getUserByManagement(String managementId) {
        List<Department> departments = departmentRepository.findByManagerId(managementId);
        List<User> userList = new ArrayList<>();
        for (Department department : departments) {
            List<User> users = userRepository.findByDepartmentAndStatus(department.getId(), 1);
            userList.addAll(users);
        }
        return userList;
    }
}
