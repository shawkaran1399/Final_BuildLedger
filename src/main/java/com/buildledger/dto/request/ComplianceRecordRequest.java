package com.buildledger.dto.request;

import com.buildledger.enums.ComplianceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ComplianceRecordRequest {
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    @NotNull(message = "Compliance type is required")
    private ComplianceType type;
    @NotBlank(message = "Result is required")
    private String result;
    @NotNull(message = "Date is required")
    private LocalDate date;
    private String notes;
}
