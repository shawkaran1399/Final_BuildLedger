package com.buildledger.dto.request;

import com.buildledger.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    @Email
    private String email;
    private String phone;
    private UserStatus status;
}
