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
import java.time.Instant;

@Entity
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalDecision decision;

    @Column(name = "decision_comment")
    private String comment;

    @Column(nullable = false)
    private Instant createdAt;

    protected Approval() {
    }

    public Approval(PurchaseRequest purchaseRequest, ApprovalDecision decision, String comment) {
        this.purchaseRequest = purchaseRequest;
        this.decision = decision;
        this.comment = comment;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public PurchaseRequest getPurchaseRequest() {
        return purchaseRequest;
    }

    public ApprovalDecision getDecision() {
        return decision;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
