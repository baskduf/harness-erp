package com.example.harnesserp.controller;

import com.example.harnesserp.dto.CreatePurchaseRequestRequest;
import com.example.harnesserp.dto.PurchaseRequestResponse;
import com.example.harnesserp.service.PurchaseRequestService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public PurchaseRequestResponse create(@Valid @RequestBody CreatePurchaseRequestRequest request) {
        return purchaseRequestService.create(request);
    }

    @GetMapping
    public List<PurchaseRequestResponse> list() {
        return purchaseRequestService.list();
    }

    @GetMapping("/{purchaseRequestId}")
    public PurchaseRequestResponse get(@PathVariable Long purchaseRequestId) {
        return purchaseRequestService.get(purchaseRequestId);
    }
}
