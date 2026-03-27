package com.buildledger.service;

import com.buildledger.dto.request.CreateUserRequest;
import com.buildledger.dto.request.UpdateUserRequest;
import com.buildledger.dto.response.UserResponse;
import com.buildledger.enums.Role;
import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(Long userId);
    UserResponse getUserByUsername(String username);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(Role role);
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId);
}
