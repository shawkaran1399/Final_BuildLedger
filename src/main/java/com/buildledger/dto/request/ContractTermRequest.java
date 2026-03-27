package com.buildledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractTermRequest {
    @NotBlank(message = "Description is required")
    private String description;
    private Boolean complianceFlag = false;
    private Integer sequenceNumber;
}
