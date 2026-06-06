package com.example.harnesserp.dto;

import com.example.harnesserp.domain.Approval;
import com.example.harnesserp.domain.ApprovalDecision;
import com.example.harnesserp.domain.PurchaseRequestStatus;
import java.time.Instant;

public record ApprovalResponse(
        Long id,
        Long purchaseRequestId,
        ApprovalDecision decision,
        PurchaseRequestStatus purchaseRequestStatus,
        String comment,
        Instant createdAt
) {
    public static ApprovalResponse from(Approval approval) {
        return new ApprovalResponse(
                approval.getId(),
                approval.getPurchaseRequest().getId(),
                approval.getDecision(),
                approval.getPurchaseRequest().getStatus(),
                approval.getComment(),
                approval.getCreatedAt()
        );
    }
}
