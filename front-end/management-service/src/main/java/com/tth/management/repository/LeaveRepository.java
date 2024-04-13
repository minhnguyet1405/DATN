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
public interface LeaveRepository extends JpaRepository<Leave, String> {
    @Query("SELECT u FROM Leave u WHERE (u.createdBy = :userId or :userId is null) and (u.status = :status or :status is null) and u.startTime >= :startTime and u.endTime <= :endTime")
    Page<Leave> getAllLeave(@Param("userId") String userId, @Param("status") String status,
                       @Param("startTime") Date email, @Param("endTime") Date endTime, Pageable pageable);
    List<Leave> findByIdIn(List<String> ids);
}
