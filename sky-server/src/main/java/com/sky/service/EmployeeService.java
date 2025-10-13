package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.Select;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);

    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    void updateEmployeeStatus(Long id, Integer status);

    Employee getEmployeeById(Integer id);

    void update(EmployeeDTO employeeDTO);
}
