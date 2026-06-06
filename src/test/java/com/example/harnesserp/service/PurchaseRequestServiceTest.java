package com.example.harnesserp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.harnesserp.domain.PurchaseRequestStatus;
import com.example.harnesserp.dto.CreateEmployeeRequest;
import com.example.harnesserp.dto.CreatePurchaseRequestRequest;
import com.example.harnesserp.dto.EmployeeResponse;
import com.example.harnesserp.dto.PurchaseRequestResponse;
import com.example.harnesserp.policy.Role;
import com.example.harnesserp.repository.PurchaseRequestRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PurchaseRequestServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PurchaseRequestService purchaseRequestService;

    @Autowired
    private PurchaseRequestRepository purchaseRequestRepository;

    @Test
    void createsSubmittedRequestByDefault() {
        PurchaseRequestResponse purchaseRequest = purchaseRequestService.create(
                Role.EMPLOYEE,
                new CreatePurchaseRequestRequest(
                        employee().id(),
                        "Keyboard",
                        new BigDecimal("85.00"),
                        null
                )
        );

        assertThat(purchaseRequest.status()).isEqualTo(PurchaseRequestStatus.SUBMITTED);
    }

    @Test
    void createsDraftRequestWhenExplicitlyRequested() {
        PurchaseRequestResponse purchaseRequest = purchaseRequestService.create(
                Role.EMPLOYEE,
                new CreatePurchaseRequestRequest(
                        employee().id(),
                        "Desk",
                        new BigDecimal("500.00"),
                        PurchaseRequestStatus.DRAFT
                )
        );

        assertThat(purchaseRequest.status()).isEqualTo(PurchaseRequestStatus.DRAFT);
    }

    @Test
    void doesNotAllowApprovedStatusAtCreation() {
        PurchaseRequestResponse purchaseRequest = purchaseRequestService.create(
                Role.EMPLOYEE,
                new CreatePurchaseRequestRequest(
                        employee().id(),
                        "Chair",
                        new BigDecimal("250.00"),
                        PurchaseRequestStatus.APPROVED
                )
        );

        assertThat(purchaseRequest.status()).isEqualTo(PurchaseRequestStatus.SUBMITTED);
    }

    @Test
    void rejectsZeroAmountInServiceLayer() {
        Long employeeId = employee().id();

        assertThatThrownBy(() -> purchaseRequestService.create(
                Role.EMPLOYEE,
                new CreatePurchaseRequestRequest(
                        employeeId,
                        "Mouse",
                        BigDecimal.ZERO,
                        null
                )
        ))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void rejectsNegativeAmountInServiceLayer() {
        Long employeeId = employee().id();

        assertThatThrownBy(() -> purchaseRequestService.create(
                Role.EMPLOYEE,
                new CreatePurchaseRequestRequest(
                        employeeId,
                        "Mouse",
                        new BigDecimal("-1.00"),
                        null
                )
        ))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void rejectsPurchaseRequestCreationForNonEmployeeBeforePersisting() {
        long purchaseRequestCount = purchaseRequestRepository.count();

        assertThatThrownBy(() -> purchaseRequestService.create(
                Role.ADMIN,
                new CreatePurchaseRequestRequest(
                        999L,
                        "Projector",
                        new BigDecimal("700.00"),
                        null
                )
        ))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("EMPLOYEE");

        assertThat(purchaseRequestRepository.count()).isEqualTo(purchaseRequestCount);
    }

    private EmployeeResponse employee() {
        return employeeService.create(Role.ADMIN, new CreateEmployeeRequest("Katherine Johnson", "Operations"));
    }
}
