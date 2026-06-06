package com.example.harnesserp.policy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AccessPolicyTest {

    private final AccessPolicy accessPolicy = new AccessPolicy();

    @Test
    void onlyAdminCanCreateEmployee() {
        assertThat(accessPolicy.canCreateEmployee(Role.ADMIN)).isTrue();
        assertThat(accessPolicy.canCreateEmployee(Role.EMPLOYEE)).isFalse();
        assertThat(accessPolicy.canCreateEmployee(Role.MANAGER)).isFalse();
        assertThat(accessPolicy.canCreateEmployee(null)).isFalse();
    }

    @Test
    void onlyEmployeeCanCreatePurchaseRequest() {
        assertThat(accessPolicy.canCreatePurchaseRequest(Role.EMPLOYEE)).isTrue();
        assertThat(accessPolicy.canCreatePurchaseRequest(Role.ADMIN)).isFalse();
        assertThat(accessPolicy.canCreatePurchaseRequest(Role.MANAGER)).isFalse();
        assertThat(accessPolicy.canCreatePurchaseRequest(null)).isFalse();
    }

    @Test
    void onlyManagerCanApproveOrRejectPurchaseRequest() {
        assertThat(accessPolicy.canApproveOrRejectPurchaseRequest(Role.MANAGER)).isTrue();
        assertThat(accessPolicy.canApproveOrRejectPurchaseRequest(Role.ADMIN)).isFalse();
        assertThat(accessPolicy.canApproveOrRejectPurchaseRequest(Role.EMPLOYEE)).isFalse();
        assertThat(accessPolicy.canApproveOrRejectPurchaseRequest(null)).isFalse();
    }
}
