package com.tth.management.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    @Id
    private String uuid;

    private String username;

    private String password;

    private String fullName;

    private String phoneNumber;

    private String email;

    private Integer gender;

//    @Temporal(TemporalType.DATE)
    private String birthday;

    private String address;

    private String avatar;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedDate;

    private String modifiedBy;

    private Integer role;

    private Department department;

    private String position;

    private Integer status; //0: lock, 1: online
}
