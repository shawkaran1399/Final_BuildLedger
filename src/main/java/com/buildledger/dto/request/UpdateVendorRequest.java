package com.buildledger.dto.request;

import com.buildledger.enums.VendorStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateVendorRequest {

    @Size(min = 2, max = 100, message = "Vendor name must be between 2 and 100 characters")
    private String name;

    @Size(max = 200, message = "Contact info cannot exceed 200 characters")
    private String contactInfo;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be valid")
    private String phone;

    @NotBlank(message = "Category is required")
    private String category;

    @Size(max = 300, message = "Address cannot exceed 300 characters")
    private String address;

    @NotNull(message = "Vendor status is required")
    private VendorStatus status;
}
