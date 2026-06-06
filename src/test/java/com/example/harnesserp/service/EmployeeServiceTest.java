package com.example.harnesserp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.harnesserp.dto.CreateEmployeeRequest;
import com.example.harnesserp.dto.EmployeeResponse;
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
        EmployeeResponse employee = employeeService.create(new CreateEmployeeRequest("Grace Hopper"));

        assertThat(employee.id()).isNotNull();
        assertThat(employee.name()).isEqualTo("Grace Hopper");
        assertThat(employeeService.list())
                .extracting(EmployeeResponse::name)
                .contains("Grace Hopper");
    }

    @Test
    void searchesEmployeesByCaseInsensitiveNameSubstring() {
        employeeService.create(new CreateEmployeeRequest("Ada Lovelace"));
        employeeService.create(new CreateEmployeeRequest("Grace Hopper"));
        employeeService.create(new CreateEmployeeRequest("Katherine Johnson"));

        assertThat(employeeService.searchByName("lov"))
                .extracting(EmployeeResponse::name)
                .containsExactly("Ada Lovelace");

        assertThat(employeeService.searchByName("HOPPER"))
                .extracting(EmployeeResponse::name)
                .containsExactly("Grace Hopper");
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
