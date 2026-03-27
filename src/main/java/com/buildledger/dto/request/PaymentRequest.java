package com.buildledger.dto.request;

import com.buildledger.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequest {
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    @NotNull(message = "Payment date is required")
    private LocalDate date;
    @NotNull(message = "Payment method is required")
    private PaymentMethod method;
    private String transactionReference;
    private String remarks;
}
