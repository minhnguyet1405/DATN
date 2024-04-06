package com.tth.id.service.impl;

import com.tth.common.utils.StringUtil;
import com.tth.id.model.Department;
import com.tth.id.repository.DepartmentRepository;
import com.tth.id.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Page<Department> getAll(Pageable pageable, String search) {
        if(StringUtil.isNullOrEmpty(search)){
            return departmentRepository.findAllByIsDeleted(pageable, 0);
        }else{
            return departmentRepository.findByKeyword(search,search,0, pageable);
        }
    }

    @Override
    public void save(Department department) {
        departmentRepository.save(department);
    }

    @Override
    public void delete(Department department) {

    }

    @Override
    public Department findByName(String name) {
        return departmentRepository.findFirstByName(name);
    }

    @Override
    public Department findByCode(String code) {
        return departmentRepository.findFirstByCode(code);
    }

    @Override
    public Department findByPhone(String phone) {
        return departmentRepository.findFirstByPhoneNumber(phone);
    }

    @Override
    public Department findById(String id) {
        return departmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Department> findByIds(List<String> ids) {
        return departmentRepository.findByIdIn(ids);
    }
}
