package com.buildledger.controller;

import com.buildledger.dto.request.ServiceRequest;
import com.buildledger.dto.response.ApiResponse;
import com.buildledger.dto.response.ServiceResponse;
import com.buildledger.enums.ServiceStatus;
import com.buildledger.service.ServiceTrackingService;
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
@RequestMapping("/services")
@RequiredArgsConstructor
@Tag(name = "Service Tracking", description = "Track service completion | VENDOR: create | PROJECT_MANAGER: mark complete/reject")
@SecurityRequirement(name = "bearerAuth")
public class ServiceController {

    private final ServiceTrackingService serviceTrackingService;

    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Create service record [VENDOR only]",
               description = "✅ Only VENDOR can submit service records.\n❌ PROJECT_MANAGER, FINANCE_OFFICER, COMPLIANCE_OFFICER cannot create service records.")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(@Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service record created", serviceTrackingService.createService(request)));
    }

    @GetMapping
    @Operation(summary = "Get all services [PUBLIC]")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getAllServices() {
        return ResponseEntity.ok(ApiResponse.success("Services retrieved", serviceTrackingService.getAllServices()));
    }

    @GetMapping("/{serviceId}")
    @Operation(summary = "Get service by ID [PUBLIC]")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceById(@PathVariable Long serviceId) {
        return ResponseEntity.ok(ApiResponse.success("Service retrieved", serviceTrackingService.getServiceById(serviceId)));
    }

    @GetMapping("/contract/{contractId}")
    @Operation(summary = "Get services by contract [PUBLIC]")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getServicesByContract(@PathVariable Long contractId) {
        return ResponseEntity.ok(ApiResponse.success("Services retrieved", serviceTrackingService.getServicesByContract(contractId)));
    }

    @PutMapping("/{serviceId}")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Update service record [VENDOR only]",
               description = "✅ Only VENDOR can update their service records.")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Service updated", serviceTrackingService.updateService(serviceId, request)));
    }

    @PatchMapping("/{serviceId}/status")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Update service status [PROJECT_MANAGER/ADMIN only]",
               description = "✅ PROJECT_MANAGER and ADMIN can mark services as COMPLETED or REJECTED.\n❌ VENDOR cannot mark their own service as complete.")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateServiceStatus(
            @PathVariable Long serviceId,
            @RequestParam ServiceStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Service status updated",
                serviceTrackingService.updateServiceStatus(serviceId, status)));
    }

    @DeleteMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete service record [ADMIN only]")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long serviceId) {
        serviceTrackingService.deleteService(serviceId);
        return ResponseEntity.ok(ApiResponse.success("Service record deleted"));
    }
}
