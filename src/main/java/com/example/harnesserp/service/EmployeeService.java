package com.example.harnesserp.service;

import com.example.harnesserp.domain.Employee;
import com.example.harnesserp.dto.CreateEmployeeRequest;
import com.example.harnesserp.dto.EmployeeResponse;
import com.example.harnesserp.repository.EmployeeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        Employee employee = new Employee(request.name());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> list() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::from)
                .toList();
    }
}
