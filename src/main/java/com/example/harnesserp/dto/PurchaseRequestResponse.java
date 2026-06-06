package com.example.harnesserp.dto;

import com.example.harnesserp.domain.PurchaseRequest;
import com.example.harnesserp.domain.PurchaseRequestStatus;
import java.math.BigDecimal;

public record PurchaseRequestResponse(
        Long id,
        Long employeeId,
        String employeeName,
        String description,
        BigDecimal amount,
        PurchaseRequestStatus status
) {
    public static PurchaseRequestResponse from(PurchaseRequest purchaseRequest) {
        return new PurchaseRequestResponse(
                purchaseRequest.getId(),
                purchaseRequest.getEmployee().getId(),
                purchaseRequest.getEmployee().getName(),
                purchaseRequest.getDescription(),
                purchaseRequest.getAmount(),
                purchaseRequest.getStatus()
        );
    }
}
