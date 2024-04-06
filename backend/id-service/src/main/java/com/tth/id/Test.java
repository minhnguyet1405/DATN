package com.tth.id;

import com.tth.id.model.dto.UserDTO;
import com.tth.id.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.UUID;

//@Controller
//public class Test {
//
//    @Autowired
//    private UserService userService;
//
//    @PostConstruct
//    public void createUser() throws ParseException {
//        String username = "thangbq";
//        String password = "thangbq";
//        String matchingPassword = "thangbq";
//        String fullName = "Bùi Quang Thắng";
//        String phoneNumber = "0344799395";
//        String email = "thangbq@elcom.com.vn";
//        Integer gender = 1;
//        String birthday = "22/03/1998";
//        String address = "Hoàng Mai, HÀ Nội";
//        String avatar = "";
//        Integer role = 1;
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.setUsername(username);
//        userDTO.setPassword(password);
//        userDTO.setMatchingPassword(matchingPassword);
//        userDTO.setFullName(fullName);
//        userDTO.setPhoneNumber(phoneNumber);
//        userDTO.setEmail(email);
//        userDTO.setGender(gender);
//        userDTO.setBirthday(birthday);
//        userDTO.setAddress(address);
//        userDTO.setAvatar(avatar);
//        userDTO.setRole(role);
//
//        userService.save(userDTO, UUID.randomUUID().toString());
//    }
//}
