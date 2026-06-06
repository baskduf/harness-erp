package com.example.harnesserp.service;

import com.example.harnesserp.domain.Employee;
import com.example.harnesserp.dto.CreateEmployeeRequest;
import com.example.harnesserp.dto.EmployeeResponse;
import com.example.harnesserp.dto.UpdateEmployeeRequest;
import com.example.harnesserp.policy.AccessPolicy;
import com.example.harnesserp.policy.Role;
import com.example.harnesserp.repository.EmployeeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AccessPolicy accessPolicy;

    public EmployeeService(EmployeeRepository employeeRepository, AccessPolicy accessPolicy) {
        this.employeeRepository = employeeRepository;
        this.accessPolicy = accessPolicy;
    }

    @Transactional
    public EmployeeResponse create(Role callerRole, CreateEmployeeRequest request) {
        requireCanCreateEmployee(callerRole);
        Employee employee = new Employee(request.name(), request.department());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(Role callerRole, Long employeeId, UpdateEmployeeRequest request) {
        requireCanUpdateEmployee(callerRole);
        Employee employee = findEmployee(employeeId);
        employee.update(request.name(), request.department());
        return EmployeeResponse.from(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> list() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchByName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("Employee search name is required");
        }

        return employeeRepository.findByNameContainingIgnoreCase(name.strip()).stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(Long employeeId) {
        return EmployeeResponse.from(findEmployee(employeeId));
    }

    private void requireCanCreateEmployee(Role callerRole) {
        if (!accessPolicy.canCreateEmployee(callerRole)) {
            throw new BusinessRuleException("ADMIN role is required to create employees");
        }
    }

    private void requireCanUpdateEmployee(Role callerRole) {
        if (!accessPolicy.canUpdateEmployee(callerRole)) {
            throw new BusinessRuleException("ADMIN role is required to update employees");
        }
    }

    private Employee findEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee " + employeeId + " was not found"
                ));
    }
}
