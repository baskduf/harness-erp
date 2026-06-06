package com.example.harnesserp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.harnesserp.domain.ApprovalDecision;
import com.example.harnesserp.domain.PurchaseRequestStatus;
import com.example.harnesserp.dto.ApprovalResponse;
import com.example.harnesserp.dto.CreateEmployeeRequest;
import com.example.harnesserp.dto.CreatePurchaseRequestRequest;
import com.example.harnesserp.dto.EmployeeResponse;
import com.example.harnesserp.dto.PurchaseRequestResponse;
import java.math.BigDecimal;
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

    @Test
    void approvesSubmittedPurchaseRequest() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.approve(purchaseRequest.id());

        assertThat(approval.decision()).isEqualTo(ApprovalDecision.APPROVED);
        assertThat(approval.purchaseRequestStatus()).isEqualTo(PurchaseRequestStatus.APPROVED);
        assertThat(approval.comment()).isNull();
    }

    @Test
    void rejectsSubmittedPurchaseRequest() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.reject(purchaseRequest.id());

        assertThat(approval.decision()).isEqualTo(ApprovalDecision.REJECTED);
        assertThat(approval.purchaseRequestStatus()).isEqualTo(PurchaseRequestStatus.REJECTED);
        assertThat(approval.comment()).isNull();
    }

    @Test
    void approvingSubmittedPurchaseRequestPersistsCommentInResponse() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.approve(
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
                purchaseRequest.id(),
                "Vendor quote is stale"
        );

        assertThat(approval.decision()).isEqualTo(ApprovalDecision.REJECTED);
        assertThat(approval.comment()).isEqualTo("Vendor quote is stale");
    }

    @Test
    void blankApprovalCommentIsNormalizedToNull() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();

        ApprovalResponse approval = approvalService.approve(purchaseRequest.id(), "   ");

        assertThat(approval.comment()).isNull();
    }

    @Test
    void doesNotApproveDraftRequest() {
        PurchaseRequestResponse purchaseRequest = draftRequest();

        assertThatThrownBy(() -> approvalService.approve(purchaseRequest.id()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SUBMITTED");
    }

    @Test
    void doesNotRejectDraftRequest() {
        PurchaseRequestResponse purchaseRequest = draftRequest();

        assertThatThrownBy(() -> approvalService.reject(purchaseRequest.id()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SUBMITTED");
    }

    @Test
    void doesNotApproveAlreadyApprovedRequest() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();
        approvalService.approve(purchaseRequest.id());

        assertThatThrownBy(() -> approvalService.approve(purchaseRequest.id()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SUBMITTED");
    }

    @Test
    void doesNotRejectAlreadyRejectedRequest() {
        PurchaseRequestResponse purchaseRequest = submittedRequest();
        approvalService.reject(purchaseRequest.id());

        assertThatThrownBy(() -> approvalService.reject(purchaseRequest.id()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("SUBMITTED");
    }

    private PurchaseRequestResponse submittedRequest() {
        return purchaseRequestService.create(new CreatePurchaseRequestRequest(
                employee().id(),
                "Laptop",
                new BigDecimal("1200.00"),
                null
        ));
    }

    private PurchaseRequestResponse draftRequest() {
        return purchaseRequestService.create(new CreatePurchaseRequestRequest(
                employee().id(),
                "Monitor",
                new BigDecimal("350.00"),
                PurchaseRequestStatus.DRAFT
        ));
    }

    private EmployeeResponse employee() {
        return employeeService.create(new CreateEmployeeRequest("Ada Lovelace", "Finance"));
    }
}
