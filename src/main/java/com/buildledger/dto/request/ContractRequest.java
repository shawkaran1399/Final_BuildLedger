package com.buildledger.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractRequest {
    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
    @NotNull(message = "Project ID is required")
    private Long projectId;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    @NotNull(message = "Contract value is required")
    @Positive(message = "Contract value must be positive")
    private BigDecimal value;
    private String description;
}
