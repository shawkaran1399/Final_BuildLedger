package com.buildledger.service;

import com.buildledger.dto.request.LoginRequest;
import com.buildledger.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
