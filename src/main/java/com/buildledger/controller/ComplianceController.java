package com.buildledger.controller;

import com.buildledger.dto.request.AuditRequest;
import com.buildledger.dto.request.ComplianceRecordRequest;
import com.buildledger.dto.response.ApiResponse;
import com.buildledger.dto.response.AuditResponse;
import com.buildledger.dto.response.ComplianceRecordResponse;
import com.buildledger.enums.AuditStatus;
import com.buildledger.service.ComplianceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Compliance & Audit", description = "Compliance monitoring and audit management")
@SecurityRequirement(name = "bearerAuth")
public class ComplianceController {

    private final ComplianceService complianceService;

    @PostMapping("/compliance")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER') or hasRole('ADMIN')")
    @Operation(summary = "Create compliance record", description = "COMPLIANCE_OFFICER/ADMIN: Record a compliance check")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> createComplianceRecord(
            @Valid @RequestBody ComplianceRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Compliance record created",
                        complianceService.createComplianceRecord(request)));
    }

    @GetMapping("/compliance")
    @Operation(summary = "Get all compliance records", description = "PUBLIC")
    public ResponseEntity<ApiResponse<List<ComplianceRecordResponse>>> getAllComplianceRecords() {
        return ResponseEntity.ok(ApiResponse.success("Compliance records retrieved",
                complianceService.getAllComplianceRecords()));
    }

    @GetMapping("/compliance/{complianceId}")
    @Operation(summary = "Get compliance record by ID")
    public ResponseEntity<ApiResponse<ComplianceRecordResponse>> getComplianceRecordById(
            @PathVariable Long complianceId) {
        return ResponseEntity.ok(ApiResponse.success("Compliance record retrieved",
                complianceService.getComplianceRecordById(complianceId)));
    }

    @GetMapping("/compliance/contract/{contractId}")
    @Operation(summary = "Get compliance records by contract")
    public ResponseEntity<ApiResponse<List<ComplianceRecordResponse>>> getComplianceRecordsByContract(
            @PathVariable Long contractId) {
        return ResponseEntity.ok(ApiResponse.success("Compliance records retrieved",
                complianceService.getComplianceRecordsByContract(contractId)));
    }

    // ---- Audits ----
    @PostMapping("/audits")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER') or hasRole('ADMIN')")
    @Operation(summary = "Create audit", description = "COMPLIANCE_OFFICER/ADMIN: Schedule a new audit")
    public ResponseEntity<ApiResponse<AuditResponse>> createAudit(
            @Valid @RequestBody AuditRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Audit created",
                        complianceService.createAudit(request, authentication.getName())));
    }

    @GetMapping("/audits")
    @Operation(summary = "Get all audits", description = "PUBLIC")
    public ResponseEntity<ApiResponse<List<AuditResponse>>> getAllAudits() {
        return ResponseEntity.ok(ApiResponse.success("Audits retrieved", complianceService.getAllAudits()));
    }

    @GetMapping("/audits/{auditId}")
    @Operation(summary = "Get audit by ID")
    public ResponseEntity<ApiResponse<AuditResponse>> getAuditById(@PathVariable Long auditId) {
        return ResponseEntity.ok(ApiResponse.success("Audit retrieved", complianceService.getAuditById(auditId)));
    }

    @PatchMapping("/audits/{auditId}/status")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER') or hasRole('ADMIN')")
    @Operation(summary = "Update audit status and findings", description = "COMPLIANCE_OFFICER/ADMIN: Progress audit lifecycle")
    public ResponseEntity<ApiResponse<AuditResponse>> updateAuditStatus(
            @PathVariable Long auditId,
            @RequestParam AuditStatus status,
            @RequestParam(required = false) String findings) {
        return ResponseEntity.ok(ApiResponse.success("Audit updated",
                complianceService.updateAuditStatus(auditId, status, findings)));
    }
}
