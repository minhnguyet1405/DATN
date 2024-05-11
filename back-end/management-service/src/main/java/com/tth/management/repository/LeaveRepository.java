package com.tth.management.repository;

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
public interface LeaveRepository extends JpaRepository<Leave, String> {
    @Query("SELECT u FROM Leave u WHERE (u.createdBy = :userId or :userId is null) and (u.type = :type or :type is null) and u.startTime >= :startTime and u.endTime <= :endTime")
    List<Leave> getAllLeave(@Param("userId") String userId, @Param("type") String type,
                            @Param("startTime") Date email, @Param("endTime") Date endTime);

    @Query("SELECT u FROM Leave u WHERE (u.createdBy in :userIds or :userIds is null) and (u.type = :type or :type is null) and u.startTime >= :startTime and u.endTime <= :endTime order by u.startTime desc ")
    Page<Leave> getAllLeave(@Param("userIds") List<String> userIds, @Param("type") String type,
                            @Param("startTime") Date email, @Param("endTime") Date endTime, Pageable pageable);

    List<Leave> findByIdIn(List<String> ids);

    Leave findFirstByCreatedByAndTypeAndStartTimeBetween(String userId, String type, Date startTime, Date endTime);

    Leave findFirstByCreatedByAndTypeAndEndTimeBetween(String userId, String type, Date startTime, Date endTime);

    @Query("SELECT count (c.id) from Leave c where c.createdBy in :userIds and (c.endTime >= :startTime and c.endTime <= :endTime) or (c.startTime >= :startTime and c.startTime <= :endTime) or (c.startTime <= :startTime and c.endTime >= :endTime)")
    long countLeavePlan(@Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    @Query("SELECT count (c.id) from Leave c where (c.endTime >= :startTime and c.endTime <= :endTime) or (c.startTime >= :startTime and c.startTime <= :endTime) or (c.startTime <= :startTime and c.endTime >= :endTime)")
    long countLeavePlan(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<Leave> findByReceive(String receive);

    @Query("SELECT c from Leave c where c.createdBy = :userId and (c.startTime <= :date and c.endTime >= :date)")
    List<Leave> findLeaveInDay(@Param("userId") String userId, @Param("date") Date date);

}
