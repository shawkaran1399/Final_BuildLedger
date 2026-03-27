package com.buildledger.repository;

import com.buildledger.entity.Audit;
import com.buildledger.enums.AuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    List<Audit> findByComplianceOfficerUserId(Long officerId);
    List<Audit> findByStatus(AuditStatus status);
}
