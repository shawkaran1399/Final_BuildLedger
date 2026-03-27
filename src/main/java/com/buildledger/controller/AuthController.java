package com.buildledger.controller;

import com.buildledger.dto.request.LoginRequest;
import com.buildledger.dto.response.ApiResponse;
import com.buildledger.dto.response.LoginResponse;
import com.buildledger.entity.User;
import com.buildledger.exception.ResourceNotFoundException;
import com.buildledger.repository.UserRepository;
import com.buildledger.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Login and session info")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(summary = "Login",
               description = "Authenticate with username and password to receive JWT token.\n\n" +
                       "**Default credentials:** username=`admin`, password=`admin123`\n\n" +
                       "After login, copy the `accessToken` and click **Authorize 🔒** at the top → enter `Bearer <token>`")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Who am I? — Current logged-in user",
               description = "🔍 Shows who is currently logged in — name, username, role, status.\n\n" +
                       "Use this to confirm which user is active after clicking **Authorize 🔒**.\n\n" +
                       "**Try this first after logging in to confirm your session!**")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("userId", user.getUserId());
        info.put("username", user.getUsername());
        info.put("name", user.getName());
        info.put("role", user.getRole());
        info.put("email", user.getEmail());
        info.put("status", user.getStatus());
        info.put("message", "You are logged in as " + user.getRole() + " (" + user.getName() + ")");

        return ResponseEntity.ok(ApiResponse.success(
                "✅ Logged in as: " + user.getName() + " [" + user.getRole() + "]", info));
    }
}
