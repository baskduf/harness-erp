package com.example.harnesserp.dto;

import com.example.harnesserp.domain.PurchaseRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreatePurchaseRequestRequest(
        @NotNull Long employeeId,
        @NotBlank String description,
        @NotNull BigDecimal amount,
        PurchaseRequestStatus status
) {
}
