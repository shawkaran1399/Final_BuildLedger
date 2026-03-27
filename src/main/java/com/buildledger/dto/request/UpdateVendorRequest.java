package com.buildledger.dto.request;

import com.buildledger.enums.VendorStatus;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateVendorRequest {
    private String name;
    private String contactInfo;
    @Email
    private String email;
    private String phone;
    private String category;
    private String address;
    private VendorStatus status;
}
