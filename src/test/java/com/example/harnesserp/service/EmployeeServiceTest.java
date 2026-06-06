package com.example.harnesserp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.harnesserp.dto.CreateEmployeeRequest;
import com.example.harnesserp.dto.EmployeeResponse;
import com.example.harnesserp.policy.Role;
import com.example.harnesserp.repository.EmployeeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void createsAndListsEmployees() {
        EmployeeResponse employee = employeeService.create(
                Role.ADMIN,
                new CreateEmployeeRequest("Grace Hopper", "Engineering")
        );

        assertThat(employee.id()).isNotNull();
        assertThat(employee.name()).isEqualTo("Grace Hopper");
        assertThat(employee.department()).isEqualTo("Engineering");
        assertThat(employeeService.list())
                .extracting(EmployeeResponse::name)
                .contains("Grace Hopper");
    }

    @Test
    void searchesEmployeesByCaseInsensitiveNameSubstring() {
        employeeService.create(Role.ADMIN, new CreateEmployeeRequest("Ada Lovelace", "Finance"));
        employeeService.create(Role.ADMIN, new CreateEmployeeRequest("Grace Hopper", "Engineering"));
        employeeService.create(Role.ADMIN, new CreateEmployeeRequest("Katherine Johnson", "Operations"));

        List<EmployeeResponse> lovelaceResults = employeeService.searchByName("lov");
        assertThat(lovelaceResults).hasSize(1);
        assertThat(lovelaceResults.get(0).name()).isEqualTo("Ada Lovelace");
        assertThat(lovelaceResults.get(0).department()).isEqualTo("Finance");

        List<EmployeeResponse> hopperResults = employeeService.searchByName("HOPPER");
        assertThat(hopperResults).hasSize(1);
        assertThat(hopperResults.get(0).name()).isEqualTo("Grace Hopper");
        assertThat(hopperResults.get(0).department()).isEqualTo("Engineering");
    }

    @Test
    void getsEmployeeDetailWithDepartment() {
        EmployeeResponse created = employeeService.create(
                Role.ADMIN,
                new CreateEmployeeRequest("Mary Jackson", "Research")
        );

        EmployeeResponse detail = employeeService.get(created.id());

        assertThat(detail.name()).isEqualTo("Mary Jackson");
        assertThat(detail.department()).isEqualTo("Research");
    }

    @Test
    void rejectsBlankDepartmentForNewEmployee() {
        assertThatThrownBy(() -> employeeService.create(
                Role.ADMIN,
                new CreateEmployeeRequest("Dorothy Vaughan", "   ")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("department");
    }

    @Test
    void rejectsEmployeeCreationForNonAdminBeforePersisting() {
        long employeeCount = employeeRepository.count();

        assertThatThrownBy(() -> employeeService.create(
                Role.EMPLOYEE,
                new CreateEmployeeRequest("Margaret Hamilton", "Engineering")
        ))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ADMIN");

        assertThat(employeeRepository.count()).isEqualTo(employeeCount);
    }

    @Test
    void rejectsEmptySearchTermInServiceLayer() {
        assertThatThrownBy(() -> employeeService.searchByName(""))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("required");
    }

    @Test
    void rejectsBlankSearchTermInServiceLayer() {
        assertThatThrownBy(() -> employeeService.searchByName("   "))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("required");
    }
}
