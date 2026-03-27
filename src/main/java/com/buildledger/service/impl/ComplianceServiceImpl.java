package com.buildledger.service.impl;

import com.buildledger.dto.request.AuditRequest;
import com.buildledger.dto.request.ComplianceRecordRequest;
import com.buildledger.dto.response.AuditResponse;
import com.buildledger.dto.response.ComplianceRecordResponse;
import com.buildledger.entity.Audit;
import com.buildledger.entity.ComplianceRecord;
import com.buildledger.entity.Contract;
import com.buildledger.entity.User;
import com.buildledger.enums.AuditStatus;
import com.buildledger.exception.ResourceNotFoundException;
import com.buildledger.repository.AuditRepository;
import com.buildledger.repository.ComplianceRecordRepository;
import com.buildledger.repository.ContractRepository;
import com.buildledger.repository.UserRepository;
import com.buildledger.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceRecordRepository complianceRecordRepository;
    private final AuditRepository auditRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;

    @Override
    public ComplianceRecordResponse createComplianceRecord(ComplianceRecordRequest request) {
        log.info("Creating compliance record for contract {}", request.getContractId());
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract", "id", request.getContractId()));

        ComplianceRecord record = ComplianceRecord.builder()
                .contract(contract)
                .type(request.getType())
                .result(request.getResult())
                .date(request.getDate())
                .notes(request.getNotes())
                .build();

        return mapRecordToResponse(complianceRecordRepository.save(record));
    }

    @Override
    @Transactional(readOnly = true)
    public ComplianceRecordResponse getComplianceRecordById(Long complianceId) {
        ComplianceRecord record = complianceRecordRepository.findById(complianceId)
                .orElseThrow(() -> new ResourceNotFoundException("ComplianceRecord", "id", complianceId));
        return mapRecordToResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplianceRecordResponse> getAllComplianceRecords() {
        return complianceRecordRepository.findAll().stream()
                .map(this::mapRecordToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplianceRecordResponse> getComplianceRecordsByContract(Long contractId) {
        return complianceRecordRepository.findByContractContractId(contractId).stream()
                .map(this::mapRecordToResponse).collect(Collectors.toList());
    }

    @Override
    public AuditResponse createAudit(AuditRequest request, String officerUsername) {
        log.info("Creating audit by officer: {}", officerUsername);
        User officer = userRepository.findByUsername(officerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", officerUsername));

        Audit audit = Audit.builder()
                .complianceOfficer(officer)
                .scope(request.getScope())
                .findings(request.getFindings())
                .date(request.getDate())
                .status(AuditStatus.SCHEDULED)
                .build();

        return mapAuditToResponse(auditRepository.save(audit));
    }

    @Override
    @Transactional(readOnly = true)
    public AuditResponse getAuditById(Long auditId) {
        return mapAuditToResponse(findAuditById(auditId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditResponse> getAllAudits() {
        return auditRepository.findAll().stream().map(this::mapAuditToResponse).collect(Collectors.toList());
    }

    @Override
    public AuditResponse updateAuditStatus(Long auditId, AuditStatus status, String findings) {
        log.info("Updating audit {} status to {}", auditId, status);
        Audit audit = findAuditById(auditId);
        audit.setStatus(status);
        if (findings != null) audit.setFindings(findings);
        return mapAuditToResponse(auditRepository.save(audit));
    }

    private Audit findAuditById(Long auditId) {
        return auditRepository.findById(auditId)
                .orElseThrow(() -> new ResourceNotFoundException("Audit", "id", auditId));
    }

    private ComplianceRecordResponse mapRecordToResponse(ComplianceRecord r) {
        return ComplianceRecordResponse.builder()
                .complianceId(r.getComplianceId())
                .contractId(r.getContract().getContractId())
                .type(r.getType())
                .result(r.getResult())
                .date(r.getDate())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private AuditResponse mapAuditToResponse(Audit a) {
        return AuditResponse.builder()
                .auditId(a.getAuditId())
                .complianceOfficerId(a.getComplianceOfficer().getUserId())
                .officerName(a.getComplianceOfficer().getName())
                .scope(a.getScope())
                .findings(a.getFindings())
                .date(a.getDate())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
