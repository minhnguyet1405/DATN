package com.tth.management.service.impl;

import com.tth.management.model.CheckInOut;
import com.tth.management.repository.CheckInOutRepository;
import com.tth.management.service.CheckInOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CheckInOutServiceImpl implements CheckInOutService {
    @Autowired
    private CheckInOutRepository checkInOutRepository;

    @Override
    public Page<CheckInOut> findAllByUserIdAndMonth(Pageable pageable, String userId, Integer month) {
        return checkInOutRepository.findAllByUserIdAndMonthOrderByDate(pageable, userId, month);
    }

    @Override
    public CheckInOut findFirstByUserIdAndDate(String userId, String date) {
        return checkInOutRepository.findFirstByUserIdAndDate(userId, date);
    }

    @Override
    public void save(CheckInOut checkInOut) {
        checkInOutRepository.save(checkInOut);
    }


}
