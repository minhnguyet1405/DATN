package com.tth.management.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "check_in_out")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInOut implements Serializable {
    @Id
    private String id;

    @Column(name = "time_in")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date timeIn;

    @Column(name = "time_out")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date timeOut;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date updateTime;

    @Column(name = "date")
    private String date;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "month")
    private Integer month;

    @Column(name = "time_late")
    private Integer timeLate;

    @Column(name = "time_soon")
    private Integer timeSoon;

    @Column(name = "reason")
    private Integer reason;
}
