package com.buildledger.controller;

import com.buildledger.dto.request.ContractRequest;
import com.buildledger.dto.request.ContractTermRequest;
import com.buildledger.dto.response.ApiResponse;
import com.buildledger.dto.response.ContractResponse;
import com.buildledger.dto.response.ContractTermResponse;
import com.buildledger.enums.ContractStatus;
import com.buildledger.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@Tag(name = "Contract Management")
@SecurityRequirement(name = "bearerAuth")
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Create contract [ADMIN, PROJECT_MANAGER]",
               description = "✅ ADMIN, PROJECT_MANAGER\n❌ VENDOR cannot create contracts.\n❌ CLIENT cannot create contracts.")
    public ResponseEntity<ApiResponse<ContractResponse>> createContract(@Valid @RequestBody ContractRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contract created successfully", contractService.createContract(request)));
    }

    @GetMapping
    @Operation(summary = "Get all contracts [ALL roles]")
    public ResponseEntity<ApiResponse<List<ContractResponse>>> getAllContracts() {
        return ResponseEntity.ok(ApiResponse.success("Contracts retrieved", contractService.getAllContracts()));
    }

    @GetMapping("/{contractId}")
    @Operation(summary = "Get contract by ID [ALL roles]")
    public ResponseEntity<ApiResponse<ContractResponse>> getContractById(@PathVariable Long contractId) {
        return ResponseEntity.ok(ApiResponse.success("Contract retrieved", contractService.getContractById(contractId)));
    }

    @GetMapping("/vendor/{vendorId}")
    @Operation(summary = "Get contracts by vendor [ALL roles]")
    public ResponseEntity<ApiResponse<List<ContractResponse>>> getContractsByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(ApiResponse.success("Contracts retrieved", contractService.getContractsByVendor(vendorId)));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get contracts by project [ALL roles]")
    public ResponseEntity<ApiResponse<List<ContractResponse>>> getContractsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.success("Contracts retrieved", contractService.getContractsByProject(projectId)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get contracts by status [ALL roles]")
    public ResponseEntity<ApiResponse<List<ContractResponse>>> getContractsByStatus(@PathVariable ContractStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Contracts retrieved", contractService.getContractsByStatus(status)));
    }

    @PutMapping("/{contractId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Update contract [ADMIN, PROJECT_MANAGER]",
               description = "✅ ADMIN, PROJECT_MANAGER\n❌ VENDOR cannot edit contracts.\n❌ CLIENT cannot edit contracts.")
    public ResponseEntity<ApiResponse<ContractResponse>> updateContract(
            @PathVariable Long contractId,
            @Valid @RequestBody ContractRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Contract updated", contractService.updateContract(contractId, request)));
    }

    @PatchMapping("/{contractId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Change contract status [ADMIN, PROJECT_MANAGER]",
               description = "✅ ADMIN, PROJECT_MANAGER\n❌ VENDOR cannot change contract status.")
    public ResponseEntity<ApiResponse<ContractResponse>> updateContractStatus(
            @PathVariable Long contractId,
            @RequestParam ContractStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Contract status updated",
                contractService.updateContractStatus(contractId, status)));
    }

    @DeleteMapping("/{contractId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Delete contract [ADMIN, PROJECT_MANAGER]",
               description = "✅ ADMIN, PROJECT_MANAGER\n❌ VENDOR cannot delete contracts.")
    public ResponseEntity<ApiResponse<Void>> deleteContract(@PathVariable Long contractId) {
        contractService.deleteContract(contractId);
        return ResponseEntity.ok(ApiResponse.success("Contract deleted successfully"));
    }

    @PostMapping("/{contractId}/terms")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Add contract term [ADMIN, PROJECT_MANAGER]",
               description = "✅ ADMIN, PROJECT_MANAGER\n❌ VENDOR cannot add contract terms.")
    public ResponseEntity<ApiResponse<ContractTermResponse>> addContractTerm(
            @PathVariable Long contractId,
            @Valid @RequestBody ContractTermRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contract term added",
                        contractService.addContractTerm(contractId, request)));
    }

    @GetMapping("/{contractId}/terms")
    @Operation(summary = "Get contract terms [ALL roles]")
    public ResponseEntity<ApiResponse<List<ContractTermResponse>>> getContractTerms(@PathVariable Long contractId) {
        return ResponseEntity.ok(ApiResponse.success("Contract terms retrieved",
                contractService.getContractTerms(contractId)));
    }

    @PutMapping("/terms/{termId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Edit contract term [ADMIN, PROJECT_MANAGER]",
               description = "✅ ADMIN, PROJECT_MANAGER\n❌ VENDOR cannot edit contract terms.")
    public ResponseEntity<ApiResponse<ContractTermResponse>> editContractTerm(
            @PathVariable Long termId,
            @Valid @RequestBody ContractTermRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Contract term updated",
                contractService.editContractTerm(termId, request)));
    }

    @DeleteMapping("/terms/{termId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Delete contract term [ADMIN, PROJECT_MANAGER]",
               description = "✅ ADMIN, PROJECT_MANAGER\n❌ VENDOR cannot delete contract terms.")
    public ResponseEntity<ApiResponse<Void>> deleteContractTerm(@PathVariable Long termId) {
        contractService.deleteContractTerm(termId);
        return ResponseEntity.ok(ApiResponse.success("Contract term deleted"));
    }
}
