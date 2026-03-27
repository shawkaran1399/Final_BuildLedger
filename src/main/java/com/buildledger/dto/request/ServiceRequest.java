package com.buildledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ServiceRequest {
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    @NotBlank(message = "Description is required")
    private String description;
    private LocalDate completionDate;
    private String remarks; //size
}
