package com.example.harnesserp.service;

import com.example.harnesserp.domain.Approval;
import com.example.harnesserp.domain.ApprovalDecision;
import com.example.harnesserp.domain.PurchaseRequest;
import com.example.harnesserp.dto.ApprovalResponse;
import com.example.harnesserp.repository.ApprovalRepository;
import com.example.harnesserp.repository.PurchaseRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApprovalService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ApprovalRepository approvalRepository;

    public ApprovalService(
            PurchaseRequestRepository purchaseRequestRepository,
            ApprovalRepository approvalRepository
    ) {
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.approvalRepository = approvalRepository;
    }

    @Transactional
    public ApprovalResponse approve(Long purchaseRequestId) {
        return approve(purchaseRequestId, null);
    }

    @Transactional
    public ApprovalResponse approve(Long purchaseRequestId, String comment) {
        PurchaseRequest purchaseRequest = findPurchaseRequest(purchaseRequestId);
        applyApprovalTransition(purchaseRequest);
        Approval approval = approvalRepository.save(
                new Approval(purchaseRequest, ApprovalDecision.APPROVED, normalizeComment(comment))
        );
        return ApprovalResponse.from(approval);
    }

    @Transactional
    public ApprovalResponse reject(Long purchaseRequestId) {
        return reject(purchaseRequestId, null);
    }

    @Transactional
    public ApprovalResponse reject(Long purchaseRequestId, String comment) {
        PurchaseRequest purchaseRequest = findPurchaseRequest(purchaseRequestId);
        applyRejectionTransition(purchaseRequest);
        Approval approval = approvalRepository.save(
                new Approval(purchaseRequest, ApprovalDecision.REJECTED, normalizeComment(comment))
        );
        return ApprovalResponse.from(approval);
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
