package com.buildledger.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceRequest {
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    @NotNull(message = "Date is required")
    private LocalDate date;
    private LocalDate dueDate;
    private String description;
}
