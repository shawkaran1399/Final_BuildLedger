package com.buildledger.repository;

import com.buildledger.entity.VendorDocument;
import com.buildledger.enums.DocumentType;
import com.buildledger.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VendorDocumentRepository extends JpaRepository<VendorDocument, Long> {
    List<VendorDocument> findByVendorVendorId(Long vendorId);
    List<VendorDocument> findByVendorVendorIdAndDocType(Long vendorId, DocumentType docType);
    List<VendorDocument> findByVerificationStatus(VerificationStatus status);
}
