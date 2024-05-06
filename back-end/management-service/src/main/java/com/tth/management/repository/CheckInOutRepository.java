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
    Page<CheckInOut> findAllByUserIdAndMonthOrderByDate(Pageable pageable, String userId, Integer month);
    CheckInOut findFirstByUserIdAndDate(String userId, String date);

    @Query("SELECT count (c.id) from CheckInOut c where c.userId in :userIds and c.updateTime >= :startTime and c.updateTime <= :endTime and ((c.timeLate is not null and c.timeLate > 0) or (c.timeSoon is not null and c.timeSoon > 0))  and c.isWeekend = false")
    long countTimeLate(@Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    @Query("SELECT count (c.id) from CheckInOut c where c.updateTime >= :startTime and c.updateTime <= :endTime and ((c.timeLate is not null and c.timeLate > 0) or (c.timeSoon is not null and c.timeSoon > 0))  and  c.isWeekend = false")
    long countTimeLate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Query("SELECT count (c.id) from CheckInOut c where c.userId in :userIds and c.updateTime >= :startTime and c.updateTime <= :endTime and (c.timeSoon is not null and c.timeSoon > 0) and c.isWeekend = false")
    long countTimeSoon(@Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    @Query("SELECT count (c.id) from CheckInOut c where c.updateTime >= :startTime and c.updateTime <= :endTime and (c.timeSoon is not null and c.timeSoon > 0)  and c.isWeekend = false")
    long countTimeSoon(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Query("SELECT count (c.id) from CheckInOut c where c.userId in :userIds and c.updateTime >= :startTime and c.updateTime <= :endTime and c.timeIn is null and c.timeOut is null and c.isWeekend = false")
    long sumCheckNull(@Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    @Query("SELECT count (c.id) from CheckInOut c where c.updateTime >= :startTime and c.updateTime <= :endTime and c.timeIn is null and c.timeOut is null and c.isWeekend = false")
    long sumCheckNull(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Query("SELECT c from CheckInOut c where c.userId in :userIds and c.updateTime >= :startTime and c.updateTime <= :endTime and c.timeIn is null and c.timeOut is null and c.isWeekend = false")
    List<CheckInOut> getLeaveReal(@Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    @Query("SELECT c from CheckInOut c where c.updateTime >= :startTime and c.updateTime <= :endTime and c.timeIn is null and c.timeOut is null and c.isWeekend = false")
    List<CheckInOut> getLeaveReal(@Param("startTime") Date startTime, @Param("endTime") Date endTime);


    CheckInOut findFirstByDateAndUserId(String time, String userId);

    @Query("select c from CheckInOut c where c.month = :month and c.userId in :userIds and c.isWeekend = :weekend and (c.timeLate > 0 or c.timeSoon > 0)")
    List<CheckInOut> findByMonthAndUserIdInAndWeekend(@Param("month") Integer month, @Param("userIds") List<String> userIds ,@Param("weekend") boolean weekend);

    @Query("select c from CheckInOut c where c.month = :month and c.isWeekend = :weekend and (c.timeLate > 0 or c.timeSoon > 0)")
    List<CheckInOut> findByMonthAndWeekend(@Param("month") Integer month, @Param("weekend") boolean weekend);

}
