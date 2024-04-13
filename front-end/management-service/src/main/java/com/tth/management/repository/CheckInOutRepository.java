package com.tth.management.repository;

import com.tth.management.model.CheckInOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInOutRepository extends JpaRepository<CheckInOut, String> {
    Page<CheckInOut> findAllByUserIdAndMonthOrderByTimeInDesc(Pageable pageable, String userId, Integer month);
    CheckInOut findFirstByUserIdAndDate(String userId, String date);
}
