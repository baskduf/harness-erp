package com.example.harnesserp.service;

import com.example.harnesserp.domain.Approval;
import com.example.harnesserp.domain.ApprovalDecision;
import com.example.harnesserp.domain.PurchaseRequest;
import com.example.harnesserp.dto.ApprovalResponse;
import com.example.harnesserp.policy.AccessPolicy;
import com.example.harnesserp.policy.Role;
import com.example.harnesserp.repository.ApprovalRepository;
import com.example.harnesserp.repository.PurchaseRequestRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApprovalService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ApprovalRepository approvalRepository;
    private final AccessPolicy accessPolicy;

    public ApprovalService(
            PurchaseRequestRepository purchaseRequestRepository,
            ApprovalRepository approvalRepository,
            AccessPolicy accessPolicy
    ) {
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.approvalRepository = approvalRepository;
        this.accessPolicy = accessPolicy;
    }

    @Transactional
    public ApprovalResponse approve(Role callerRole, Long purchaseRequestId, String comment) {
        requireCanApproveOrReject(callerRole);
        PurchaseRequest purchaseRequest = findPurchaseRequest(purchaseRequestId);
        applyApprovalTransition(purchaseRequest);
        Approval approval = approvalRepository.save(
                new Approval(purchaseRequest, ApprovalDecision.APPROVED, normalizeComment(comment))
        );
        return ApprovalResponse.from(approval);
    }

    @Transactional
    public ApprovalResponse reject(Role callerRole, Long purchaseRequestId, String comment) {
        requireCanApproveOrReject(callerRole);
        PurchaseRequest purchaseRequest = findPurchaseRequest(purchaseRequestId);
        applyRejectionTransition(purchaseRequest);
        Approval approval = approvalRepository.save(
                new Approval(purchaseRequest, ApprovalDecision.REJECTED, normalizeComment(comment))
        );
        return ApprovalResponse.from(approval);
    }

    @Transactional(readOnly = true)
    public List<ApprovalResponse> history(Long purchaseRequestId) {
        findPurchaseRequest(purchaseRequestId);
        return approvalRepository.findByPurchaseRequestIdOrderByCreatedAtAscIdAsc(purchaseRequestId).stream()
                .map(ApprovalResponse::from)
                .toList();
    }

    private void requireCanApproveOrReject(Role callerRole) {
        if (!accessPolicy.canApproveOrRejectPurchaseRequest(callerRole)) {
            throw new BusinessRuleException("MANAGER role is required to approve or reject purchase requests");
        }
    }

    private PurchaseRequest findPurchaseRequest(Long purchaseRequestId) {
        return purchaseRequestRepository.findById(purchaseRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase request " + purchaseRequestId + " was not found"
                ));
    }

    private void applyApprovalTransition(PurchaseRequest purchaseRequest) {
        try {
            purchaseRequest.approve();
        } catch (IllegalStateException exception) {
            throw new BusinessRuleException(exception.getMessage(), exception);
        }
    }

    private void applyRejectionTransition(PurchaseRequest purchaseRequest) {
        try {
            purchaseRequest.reject();
        } catch (IllegalStateException exception) {
            throw new BusinessRuleException(exception.getMessage(), exception);
        }
    }

    private String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }
        return comment.strip();
    }
}
