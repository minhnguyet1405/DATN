package com.tth.id.service;

import com.tth.id.model.Department;
import com.tth.id.model.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {
    Page<Department> getAll(Pageable pageable, String search);
    void save(Department department);
    void delete(Department department);
    Department findByName(String name);
    Department findByCode(String code);
    Department findByPhone(String phone);
    Department findById(String id);
    List<Department> findByIds(List<String> ids);
}
