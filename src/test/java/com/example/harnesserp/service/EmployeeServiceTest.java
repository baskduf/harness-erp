package com.example.harnesserp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.harnesserp.dto.CreateEmployeeRequest;
import com.example.harnesserp.dto.EmployeeResponse;
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

    @Test
    void createsAndListsEmployees() {
        EmployeeResponse employee = employeeService.create(
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
        employeeService.create(new CreateEmployeeRequest("Ada Lovelace", "Finance"));
        employeeService.create(new CreateEmployeeRequest("Grace Hopper", "Engineering"));
        employeeService.create(new CreateEmployeeRequest("Katherine Johnson", "Operations"));

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
                new CreateEmployeeRequest("Mary Jackson", "Research")
        );

        EmployeeResponse detail = employeeService.get(created.id());

        assertThat(detail.name()).isEqualTo("Mary Jackson");
        assertThat(detail.department()).isEqualTo("Research");
    }

    @Test
    void rejectsBlankDepartmentForNewEmployee() {
        assertThatThrownBy(() -> employeeService.create(
                new CreateEmployeeRequest("Dorothy Vaughan", "   ")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("department");
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
