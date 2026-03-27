package com.buildledger.repository;

import com.buildledger.entity.Contract;
import com.buildledger.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByVendorVendorId(Long vendorId);
    List<Contract> findByProjectProjectId(Long projectId);
    List<Contract> findByStatus(ContractStatus status);
    @Query("SELECT c FROM Contract c WHERE c.endDate < :date AND c.status = com.buildledger.enums.ContractStatus.ACTIVE")
    List<Contract> findExpiredActiveContracts(@Param("date") LocalDate date);
    @Query("SELECT c FROM Contract c WHERE c.endDate BETWEEN :start AND :end AND c.status = com.buildledger.enums.ContractStatus.ACTIVE")
    List<Contract> findContractsExpiringBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
