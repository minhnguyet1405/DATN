package com.tth.management.service;

import com.tth.management.model.CheckInOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CheckInOutService {
    Page<CheckInOut> findAllByUserIdAndMonth(Pageable pageable, String userId, Integer month);

    CheckInOut findFirstByUserIdAndDate(String userId, String date);

    void save(CheckInOut checkInOut);
}
