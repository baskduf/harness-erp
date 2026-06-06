package com.example.harnesserp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseRequestStatus status;

    protected PurchaseRequest() {
    }

    public PurchaseRequest(
            Employee employee,
            String description,
            BigDecimal amount,
            PurchaseRequestStatus requestedStatus
    ) {
        if (employee == null) {
            throw new IllegalArgumentException("employee is required");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount is required");
        }
        this.employee = employee;
        this.description = requireText(description, "description");
        this.amount = amount;
        this.status = requestedStatus == PurchaseRequestStatus.DRAFT
                ? PurchaseRequestStatus.DRAFT
                : PurchaseRequestStatus.SUBMITTED;
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PurchaseRequestStatus getStatus() {
        return status;
    }

    public void approve() {
        requireSubmitted("approved");
        this.status = PurchaseRequestStatus.APPROVED;
    }

    public void reject() {
        requireSubmitted("rejected");
        this.status = PurchaseRequestStatus.REJECTED;
    }

    private void requireSubmitted(String targetState) {
        if (status != PurchaseRequestStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Only SUBMITTED purchase requests can be " + targetState
            );
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.strip();
    }
}
