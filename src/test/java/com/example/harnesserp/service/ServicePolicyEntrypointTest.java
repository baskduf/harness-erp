package com.example.harnesserp.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.harnesserp.policy.Role;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServicePolicyEntrypointTest {

    @Test
    void employeeCreationEntrypointsRequireCallerRole() {
        assertPublicMutatingEntrypointsRequireRole(EmployeeService.class, "create", "update");
    }

    @Test
    void purchaseRequestCreationEntrypointsRequireCallerRole() {
        assertPublicMutatingEntrypointsRequireRole(PurchaseRequestService.class, "create");
    }

    @Test
    void approvalDecisionEntrypointsRequireCallerRole() {
        assertPublicMutatingEntrypointsRequireRole(ApprovalService.class, "approve", "reject");
    }

    private void assertPublicMutatingEntrypointsRequireRole(
            Class<?> serviceType,
            String... methodNames
    ) {
        Set<String> targetNames = Set.of(methodNames);
        var methods = Arrays.stream(serviceType.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> targetNames.contains(method.getName()))
                .toList();

        assertThat(methods)
                .describedAs("public mutating entrypoints on %s", serviceType.getSimpleName())
                .isNotEmpty()
                .allSatisfy(this::assertHasExplicitRoleParameter);
    }

    private void assertHasExplicitRoleParameter(Method method) {
        assertThat(Arrays.asList(method.getParameterTypes()))
                .describedAs("%s must require an explicit caller role", method)
                .contains(Role.class);
    }
}
