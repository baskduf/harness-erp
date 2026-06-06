package com.example.harnesserp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.harnesserp.dto.CreateEmployeeRequest;
import com.example.harnesserp.dto.EmployeeResponse;
import com.example.harnesserp.dto.UpdateEmployeeRequest;
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
    void updatesEmployeeNameAndDepartment() {
        EmployeeResponse created = employeeService.create(
                Role.ADMIN,
                new CreateEmployeeRequest("Mary Jackson", "Research")
        );

        EmployeeResponse updated = employeeService.update(
                Role.ADMIN,
                created.id(),
                new UpdateEmployeeRequest("Mary Winston Jackson", "Engineering")
        );

        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.name()).isEqualTo("Mary Winston Jackson");
        assertThat(updated.department()).isEqualTo("Engineering");
    }

    @Test
    void listDetailAndSearchReflectUpdatedEmployeeValues() {
        EmployeeResponse created = employeeService.create(
                Role.ADMIN,
                new CreateEmployeeRequest("Mary Jackson", "Research")
        );

        employeeService.update(
                Role.ADMIN,
                created.id(),
                new UpdateEmployeeRequest("Mary Winston Jackson", "Engineering")
        );

        assertThat(employeeService.get(created.id()).department()).isEqualTo("Engineering");
        assertThat(employeeService.list())
                .filteredOn(employee -> employee.id().equals(created.id()))
                .singleElement()
                .satisfies(employee -> {
                    assertThat(employee.name()).isEqualTo("Mary Winston Jackson");
                    assertThat(employee.department()).isEqualTo("Engineering");
                });
        assertThat(employeeService.searchByName("Winston"))
                .singleElement()
                .satisfies(employee -> {
                    assertThat(employee.id()).isEqualTo(created.id());
                    assertThat(employee.department()).isEqualTo("Engineering");
                });
        assertThat(employeeService.searchByName("Jackson"))
                .extracting(EmployeeResponse::name)
                .contains("Mary Winston Jackson");
    }

    @Test
    void rejectsBlankNameForEmployeeUpdateInServiceLayer() {
        EmployeeResponse created = employeeService.create(
                Role.ADMIN,
                new CreateEmployeeRequest("Dorothy Vaughan", "Computing")
        );

        assertThatThrownBy(() -> employeeService.update(
                Role.ADMIN,
                created.id(),
                new UpdateEmployeeRequest("   ", "Engineering")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void rejectsBlankDepartmentForEmployeeUpdateInServiceLayer() {
        EmployeeResponse created = employeeService.create(
                Role.ADMIN,
                new CreateEmployeeRequest("Dorothy Vaughan", "Computing")
        );

        assertThatThrownBy(() -> employeeService.update(
                Role.ADMIN,
                created.id(),
                new UpdateEmployeeRequest("Dorothy Vaughan", "   ")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("department");
    }

    @Test
    void rejectsUnknownEmployeeUpdateInServiceLayer() {
        assertThatThrownBy(() -> employeeService.update(
                Role.ADMIN,
                999L,
                new UpdateEmployeeRequest("Unknown Employee", "Operations")
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee 999");
    }

    @Test
    void rejectsEmployeeUpdateForNonAdminBeforeChangingValues() {
        EmployeeResponse created = employeeService.create(
                Role.ADMIN,
                new CreateEmployeeRequest("Margaret Hamilton", "Engineering")
        );

        assertThatThrownBy(() -> employeeService.update(
                Role.EMPLOYEE,
                created.id(),
                new UpdateEmployeeRequest("Margaret Heafield Hamilton", "Research")
        ))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ADMIN");

        EmployeeResponse unchanged = employeeService.get(created.id());
        assertThat(unchanged.name()).isEqualTo("Margaret Hamilton");
        assertThat(unchanged.department()).isEqualTo("Engineering");
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
