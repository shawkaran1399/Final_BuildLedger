package com.buildledger.service;

import com.buildledger.dto.request.AuditRequest;
import com.buildledger.dto.request.ComplianceRecordRequest;
import com.buildledger.dto.response.AuditResponse;
import com.buildledger.dto.response.ComplianceRecordResponse;
import com.buildledger.enums.AuditStatus;
import java.util.List;

public interface ComplianceService {
    ComplianceRecordResponse createComplianceRecord(ComplianceRecordRequest request);
    ComplianceRecordResponse getComplianceRecordById(Long complianceId);
    List<ComplianceRecordResponse> getAllComplianceRecords();
    List<ComplianceRecordResponse> getComplianceRecordsByContract(Long contractId);
    AuditResponse createAudit(AuditRequest request, String officerUsername);
    AuditResponse getAuditById(Long auditId);
    List<AuditResponse> getAllAudits();
    AuditResponse updateAuditStatus(Long auditId, AuditStatus status, String findings);
}
