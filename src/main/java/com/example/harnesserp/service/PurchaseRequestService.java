package com.example.harnesserp.service;

import com.example.harnesserp.domain.Employee;
import com.example.harnesserp.domain.PurchaseRequest;
import com.example.harnesserp.dto.CreatePurchaseRequestRequest;
import com.example.harnesserp.dto.PurchaseRequestResponse;
import com.example.harnesserp.repository.EmployeeRepository;
import com.example.harnesserp.repository.PurchaseRequestRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final EmployeeRepository employeeRepository;

    public PurchaseRequestService(
            PurchaseRequestRepository purchaseRequestRepository,
            EmployeeRepository employeeRepository
    ) {
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public PurchaseRequestResponse create(CreatePurchaseRequestRequest request) {
        requirePositiveAmount(request.amount());

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee " + request.employeeId() + " was not found"
                ));

        PurchaseRequest purchaseRequest = new PurchaseRequest(
                employee,
                request.description(),
                request.amount(),
                request.status()
        );
        return PurchaseRequestResponse.from(purchaseRequestRepository.save(purchaseRequest));
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequestResponse> list() {
        return purchaseRequestRepository.findAll().stream()
                .map(PurchaseRequestResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PurchaseRequestResponse get(Long purchaseRequestId) {
        return purchaseRequestRepository.findById(purchaseRequestId)
                .map(PurchaseRequestResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase request " + purchaseRequestId + " was not found"
                ));
    }

    private void requirePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessRuleException("Purchase request amount must be positive");
        }
    }
}
