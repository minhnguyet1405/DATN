package com.tth.management.repository;

import com.tth.management.model.NumberLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NumberLeaveRepository extends JpaRepository<NumberLeave, String> {
    NumberLeave findFirstByUserId(String id);
}
