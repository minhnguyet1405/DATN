package com.tth.management.repository;

import com.tth.management.model.CheckInOut;
import com.tth.management.model.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CheckInOutRepository extends JpaRepository<CheckInOut, String> {
    Page<CheckInOut> findAllByUserIdAndMonthOrderByTimeInDesc(Pageable pageable, String userId, Integer month);
    CheckInOut findFirstByUserIdAndDate(String userId, String date);
    @Query("SELECT sum (c.timeLate) from CheckInOut c where c.userId in :userIds and c.timeIn >= :startTime and c.timeOut <= :endTime")
    long sumTimeLate(@Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    @Query("SELECT sum (c.timeLate) from CheckInOut c where c.timeIn >= :startTime and c.timeOut <= :endTime")
    long sumTimeLate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Query("SELECT sum (c.timeSoon) from CheckInOut c where c.userId in :userIds and c.timeIn >= :startTime and c.timeOut <= :endTime")
    long sumTimeSoon(@Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    @Query("SELECT sum (c.timeSoon) from CheckInOut c where c.timeIn >= :startTime and c.timeOut <= :endTime")
    long sumTimeSoon(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Query("SELECT count (c.id) from CheckInOut c where c.userId in :userIds and c.updateTime >= :startTime and c.updateTime <= :endTime and c.timeIn is null and c.timeOut is null and c.isWeekend = false")
    long sumCheckNull(@Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    @Query("SELECT count (c.id) from CheckInOut c where c.updateTime >= :startTime and c.updateTime <= :endTime and c.timeIn is null and c.timeOut is null and c.isWeekend = false")
    long sumCheckNull(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    CheckInOut findFirstByDateAndUserId(String time, String userId);

    List<CheckInOut> findByMonth(Integer month);

}
