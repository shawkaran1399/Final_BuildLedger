package com.buildledger.repository;

import com.buildledger.entity.ContractTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractTermRepository extends JpaRepository<ContractTerm, Long> {
    List<ContractTerm> findByContractContractId(Long contractId);
    List<ContractTerm> findByContractContractIdAndComplianceFlagTrue(Long contractId);
}
