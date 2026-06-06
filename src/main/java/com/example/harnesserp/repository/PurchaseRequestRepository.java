package com.example.harnesserp.repository;

import com.example.harnesserp.domain.PurchaseRequest;
import com.example.harnesserp.domain.PurchaseRequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    List<PurchaseRequest> findByEmployeeId(Long employeeId);

    List<PurchaseRequest> findByStatus(PurchaseRequestStatus status);

    List<PurchaseRequest> findByEmployeeIdAndStatus(Long employeeId, PurchaseRequestStatus status);
}
