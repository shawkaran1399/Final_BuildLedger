package com.buildledger.dto.request;

import com.buildledger.enums.DocumentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VendorDocumentRequest {

    @NotNull(message = "Document type is required")
    private DocumentType docType;

    @Size(max = 250, message = "Remarks cannot exceed 250 characters")
    private String remarks;
}
