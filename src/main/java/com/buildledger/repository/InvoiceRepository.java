package com.buildledger.repository;

import com.buildledger.entity.Invoice;
import com.buildledger.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByContractContractId(Long contractId);
    List<Invoice> findByStatus(InvoiceStatus status);
    @Query("SELECT COALESCE(SUM(i.amount),0) FROM Invoice i WHERE i.contract.contractId = :contractId AND i.status = com.buildledger.enums.InvoiceStatus.PAID")
    BigDecimal sumPaidAmountByContractId(@Param("contractId") Long contractId);
}
