package com.example.harnesserp.policy;

import org.springframework.stereotype.Service;

@Service
public class AccessPolicy {

    public boolean canCreateEmployee(Role role) {
        return role == Role.ADMIN;
    }

    public boolean canUpdateEmployee(Role role) {
        return role == Role.ADMIN;
    }

    public boolean canCreatePurchaseRequest(Role role) {
        return role == Role.EMPLOYEE;
    }

    public boolean canApproveOrRejectPurchaseRequest(Role role) {
        return role == Role.MANAGER;
    }
}
