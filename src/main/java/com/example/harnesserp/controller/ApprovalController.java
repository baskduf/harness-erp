package com.example.harnesserp.controller;

import com.example.harnesserp.dto.ApprovalActionRequest;
import com.example.harnesserp.dto.ApprovalResponse;
import com.example.harnesserp.policy.Role;
import com.example.harnesserp.service.ApprovalService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase-requests/{purchaseRequestId}")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping("/approve")
    public ApprovalResponse approve(
            @PathVariable Long purchaseRequestId,
            @RequestHeader("X-ERP-Role") Role callerRole,
            @RequestBody(required = false) ApprovalActionRequest request
    ) {
        return approvalService.approve(callerRole, purchaseRequestId, commentFrom(request));
    }

    @PostMapping("/reject")
    public ApprovalResponse reject(
            @PathVariable Long purchaseRequestId,
            @RequestHeader("X-ERP-Role") Role callerRole,
            @RequestBody(required = false) ApprovalActionRequest request
    ) {
        return approvalService.reject(callerRole, purchaseRequestId, commentFrom(request));
    }

    private String commentFrom(ApprovalActionRequest request) {
        return request == null ? null : request.comment();
    }
}
