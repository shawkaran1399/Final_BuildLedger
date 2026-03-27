package com.buildledger.repository;

import com.buildledger.entity.ComplianceRecord;
import com.buildledger.enums.ComplianceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {
    List<ComplianceRecord> findByContractContractId(Long contractId);
    List<ComplianceRecord> findByType(ComplianceType type);
    List<ComplianceRecord> findByContractContractIdAndType(Long contractId, ComplianceType type);
}
