package com.example.harnesserp.dto;

import com.example.harnesserp.domain.Employee;

public record EmployeeResponse(Long id, String name) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getName());
    }
}
