package com.example.harnesserp.controller;

import com.example.harnesserp.domain.PurchaseRequestStatus;
import com.example.harnesserp.dto.CreatePurchaseRequestRequest;
import com.example.harnesserp.dto.PurchaseRequestResponse;
import com.example.harnesserp.policy.Role;
import com.example.harnesserp.service.PurchaseRequestService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase-requests")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    public PurchaseRequestController(PurchaseRequestService purchaseRequestService) {
        this.purchaseRequestService = purchaseRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseRequestResponse create(
            @RequestHeader("X-ERP-Role") Role callerRole,
            @Valid @RequestBody CreatePurchaseRequestRequest request
    ) {
        return purchaseRequestService.create(callerRole, request);
    }

    @GetMapping
    public List<PurchaseRequestResponse> list(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) PurchaseRequestStatus status
    ) {
        return purchaseRequestService.list(employeeId, status);
    }

    @GetMapping("/{purchaseRequestId}")
    public PurchaseRequestResponse get(@PathVariable Long purchaseRequestId) {
        return purchaseRequestService.get(purchaseRequestId);
    }
}
