package com.buildledger.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateVendorRequest {

    @NotBlank(message = "Vendor name is required")
    @Size(min = 3, max = 100, message = "Vendor name must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 &.-]+$", message = "Vendor name contains invalid characters")
    private String name;

    @Size(max = 255, message = "Contact info cannot exceed 255 characters")
    private String contactInfo;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid Indian phone number")
    private String phone;

    @NotBlank(message = "Category is required")
    private String category;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;
}
