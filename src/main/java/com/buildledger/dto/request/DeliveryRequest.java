package com.buildledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeliveryRequest {
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    @NotNull(message = "Date is required")
    private LocalDate date;
    @NotBlank(message = "Item is required")
    private String item;
    private BigDecimal quantity;
    private String unit;
    private String remarks;
}
