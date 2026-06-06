package com.example.harnesserp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.harnesserp.domain.Approval;
import com.example.harnesserp.domain.ApprovalDecision;
import com.example.harnesserp.domain.PurchaseRequest;
import com.example.harnesserp.domain.PurchaseRequestStatus;
import com.example.harnesserp.dto.ApprovalResponse;
import com.example.harnesserp.dto.CreateEmployeeRequest;
import com.example.harnesserp.dto.CreatePurchaseRequestRequest;
import com.example.harnesserp.dto.EmployeeResponse;
import com.example.harnesserp.dto.PurchaseRequestResponse;
import com.example.harnesserp.policy.Role;
import com.example.harnesserp.repository.ApprovalRepository;
import com.example.harnesserp.repository.PurchaseRequestRepository;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ApprovalServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PurchaseRequestService purchaseRequestService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private PurchaseRequestRepository purchaseRequestRepository;

    @Test
    void approvesSubmittedPurchaseRequest() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.approve(Role.MANAGER, purchaseRequest.id(), null);

        assertThat(approval.decision()).isEqualTo(ApprovalDecision.APPROVED);
        assertThat(approval.purchaseRequestStatus()).isEqualTo(PurchaseRequestStatus.APPROVED);
        assertThat(approval.comment()).isNull();
    }

    @Test
    void rejectsSubmittedPurchaseRequest() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.reject(Role.MANAGER, purchaseRequest.id(), null);

        assertThat(approval.decision()).isEqualTo(ApprovalDecision.REJECTED);
        assertThat(approval.purchaseRequestStatus()).isEqualTo(PurchaseRequestStatus.REJECTED);
        assertThat(approval.comment()).isNull();
    }

    @Test
    void approvingSubmittedPurchaseRequestPersistsCommentInResponse() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.approve(
                Role.MANAGER,
                purchaseRequest.id(),
                "Budget confirmed"
        );

        assertThat(approval.decision()).isEqualTo(ApprovalDecision.APPROVED);
        assertThat(approval.comment()).isEqualTo("Budget confirmed");
    }

    @Test
    void rejectingSubmittedPurchaseRequestPersistsCommentInResponse() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.reject(
                Role.MANAGER,
                purchaseRequest.id(),
                "Vendor quote is stale"
        );

        assertThat(approval.decision()).isEqualTo(ApprovalDecision.REJECTED);
        assertThat(approval.comment()).isEqualTo("Vendor quote is stale");
    }

    @Test
    void blankApprovalCommentIsNormalizedToNull() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.approve(Role.MANAGER, purchaseRequest.id(), "   ");

        assertThat(approval.comment()).isNull();
    }

    @Test
    void doesNotApproveDraftRequest() {
        PurchaseRequestResponse purchaseRequest = draftRequest();

        assertThatThrownBy(() -> approvalService.approve(Role.MANAGER, purchaseRequest.id(), null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SUBMITTED");
    }

    @Test
    void doesNotRejectDraftRequest() {
        PurchaseRequestResponse purchaseRequest = draftRequest();

        assertThatThrownBy(() -> approvalService.reject(Role.MANAGER, purchaseRequest.id(), null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SUBMITTED");
    }

    @Test
    void doesNotApproveAlreadyApprovedRequest() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();
        approvalService.approve(Role.MANAGER, purchaseRequest.id(), null);

        assertThatThrownBy(() -> approvalService.approve(Role.MANAGER, purchaseRequest.id(), null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SUBMITTED");
    }

    @Test
    void doesNotRejectAlreadyRejectedRequest() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();
        approvalService.reject(Role.MANAGER, purchaseRequest.id(), null);

        assertThatThrownBy(() -> approvalService.reject(Role.MANAGER, purchaseRequest.id(), null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SUBMITTED");
    }

    @Test
    void rejectsApprovalForNonManagerBeforePersistingOrChangingStatus() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();
        long approvalCount = approvalRepository.count();

        assertThatThrownBy(() -> approvalService.approve(
                Role.EMPLOYEE,
                purchaseRequest.id(),
                "Budget confirmed"
        ))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("MANAGER");

        assertThat(approvalRepository.count()).isEqualTo(approvalCount);
        assertThat(purchaseRequestService.get(purchaseRequest.id()).status())
                .isEqualTo(PurchaseRequestStatus.SUBMITTED);
    }

    @Test
    void rejectsRejectionForNonManagerBeforePersistingOrChangingStatus() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();
        long approvalCount = approvalRepository.count();

        assertThatThrownBy(() -> approvalService.reject(
                Role.ADMIN,
                purchaseRequest.id(),
                "Vendor quote is stale"
        ))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("MANAGER");

        assertThat(approvalRepository.count()).isEqualTo(approvalCount);
        assertThat(purchaseRequestService.get(purchaseRequest.id()).status())
                .isEqualTo(PurchaseRequestStatus.SUBMITTED);
    }

    @Test
    void listsApprovalHistoryWithPersistedCommentAndCreationTime() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.approve(
                Role.MANAGER,
                purchaseRequest.id(),
                "Budget confirmed"
        );

        assertThat(approvalService.history(purchaseRequest.id()))
                .singleElement()
                .satisfies(history -> {
                    assertThat(history.id()).isEqualTo(approval.id());
                    assertThat(history.purchaseRequestId()).isEqualTo(purchaseRequest.id());
                    assertThat(history.decision()).isEqualTo(ApprovalDecision.APPROVED);
                    assertThat(history.comment()).isEqualTo("Budget confirmed");
                    assertThat(history.createdAt()).isNotNull();
                });
    }

    @Test
    void listsApprovalHistoryInChronologicalOrderWithIdTieBreaker() throws Exception {
        PurchaseRequest purchaseRequest = findPurchaseRequest(submittedRequest().id());
        Instant createdAt = Instant.parse("2026-06-06T00:00:00Z");
        Approval first = approvalRecord(purchaseRequest, ApprovalDecision.APPROVED, "first", createdAt);
        Approval second = approvalRecord(purchaseRequest, ApprovalDecision.REJECTED, "second", createdAt);

        assertThat(approvalService.history(purchaseRequest.getId()))
                .extracting(ApprovalResponse::id)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void purchaseRequestWithoutApprovalsReturnsEmptyHistory() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        assertThat(approvalService.history(purchaseRequest.id())).isEmpty();
    }

    @Test
    void unknownPurchaseRequestHistoryIsRejectedByServiceLayer() {
        assertThatThrownBy(() -> approvalService.history(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Purchase request 999");
    }

    private PurchaseRequestResponse submittedRequest() {
        return purchaseRequestService.create(
                Role.EMPLOYEE,
                new CreatePurchaseRequestRequest(
                        employee().id(),
                        "Laptop",
                        new BigDecimal("1200.00"),
                        null
                )
        );
    }

    private PurchaseRequestResponse draftRequest() {
        return purchaseRequestService.create(
                Role.EMPLOYEE,
                new CreatePurchaseRequestRequest(
                        employee().id(),
                        "Monitor",
                        new BigDecimal("350.00"),
                        PurchaseRequestStatus.DRAFT
                )
        );
    }

    private EmployeeResponse employee() {
        return employeeService.create(Role.ADMIN, new CreateEmployeeRequest("Ada Lovelace", "Finance"));
    }

    private PurchaseRequest findPurchaseRequest(Long purchaseRequestId) {
        return purchaseRequestRepository.findById(purchaseRequestId)
                .orElseThrow();
    }

    private Approval approvalRecord(
            PurchaseRequest purchaseRequest,
            ApprovalDecision decision,
            String comment,
            Instant createdAt
    ) throws Exception {
        Approval approval = new Approval(purchaseRequest, decision, comment);
        setCreatedAt(approval, createdAt);
        return approvalRepository.save(approval);
    }

    private void setCreatedAt(Approval approval, Instant createdAt) throws Exception {
        Field createdAtField = Approval.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(approval, createdAt);
    }
}
