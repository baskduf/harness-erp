package com.example.harnesserp.repository;

import com.example.harnesserp.domain.Approval;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByPurchaseRequestIdOrderByCreatedAtAscIdAsc(Long purchaseRequestId);
}
