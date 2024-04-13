package com.tth.id.repository;

import com.tth.id.model.Department;
import com.tth.id.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    Page<Department> findAllByIsDeleted(Pageable pageable, Integer isDeleted);

    Department findFirstByName(String name);

    Department findFirstByCode(String code);

    Department findFirstByPhoneNumber(String phoneNumber);

    List<Department> findByIdIn(List<String> ids);

    @Query("SELECT d FROM Department d WHERE (d.name like %:name% OR d.code like %:code% ) AND d.isDeleted = :isDeleted")
    Page<Department> findByKeyword(@Param("name") String name, @Param("code") String code,
                             @Param("isDeleted") Integer isDeleted, Pageable pageable);
}
