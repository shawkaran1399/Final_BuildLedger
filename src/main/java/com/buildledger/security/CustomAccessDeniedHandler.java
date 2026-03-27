package com.buildledger.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentRole = "UNKNOWN";
        String currentUser = "unknown";

        if (auth != null) {
            currentUser = auth.getName();
            currentRole = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(a -> a.replace("ROLE_", ""))
                    .findFirst()
                    .orElse("UNKNOWN");
        }

        String method  = request.getMethod();
        String path    = request.getRequestURI().replace(request.getContextPath(), "");
        String normalized = path.replaceAll("/\\d+", "");

        String specificMessage = resolveMessage(method, normalized, currentRole);

        log.warn("Access denied — user='{}' role='{}' tried {} {}", currentUser, currentRole, method, path);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success",     false);
        body.put("status",      403);
        body.put("error",       "Access Denied");
        body.put("loggedInAs",  currentUser + " [" + currentRole + "]");
        body.put("message",     "❌ " + specificMessage);
        body.put("hint",        "You are logged in as " + currentRole +
                                ". Please log in with an account that has the required role.");
        body.put("path",        path);
        body.put("timestamp",   LocalDateTime.now().toString());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    private String resolveMessage(String method, String path, String role) {

        // PROJECTS
        if (path.startsWith("/projects") && !method.equals("GET"))
            return "Only ADMIN can create, edit, or delete projects. You are " + role + ".";

        // VENDORS
        if (path.startsWith("/vendors") && !method.equals("GET"))
            return "Only ADMIN can add, edit, or remove vendors. You are " + role + ".";

        // USERS
        if (path.startsWith("/users") && !method.equals("GET"))
            return "Only ADMIN can create or delete users. You are " + role + ".";

        // CONTRACTS — create
        if (path.equals("/contracts") && method.equals("POST"))
            return "Only ADMIN and PROJECT_MANAGER can create contracts. VENDOR cannot create contracts. You are " + role + ".";

        // CONTRACTS — edit/delete/status
        if (path.startsWith("/contracts") && !method.equals("GET"))
            return "Only ADMIN and PROJECT_MANAGER can edit, delete, or change contract status. You are " + role + ".";

        // CONTRACT TERMS — add/edit/delete
        if (path.contains("/terms") && !method.equals("GET"))
            return "Only ADMIN and PROJECT_MANAGER can manage contract terms. You are " + role + ".";

        // DELIVERIES — status change
        if (path.startsWith("/deliveries") && method.equals("PATCH"))
            return "Only ADMIN and PROJECT_MANAGER can change delivery status. You are " + role + ".";

        // DELIVERIES — delete
        if (path.startsWith("/deliveries") && method.equals("DELETE"))
            return "Only ADMIN and PROJECT_MANAGER can delete deliveries. You are " + role + ".";

        // INVOICES — approve/reject
        if (path.contains("/approve") || path.contains("/reject"))
            return "Only FINANCE_OFFICER can approve or reject invoices. You are " + role + ".";

        // INVOICES — submit
        if (path.startsWith("/invoices") && method.equals("POST"))
            return "Only VENDOR can submit invoices. You are " + role + ".";

        // PAYMENTS
        if (path.startsWith("/payments") && !method.equals("GET"))
            return "Only FINANCE_OFFICER can process or update payments. You are " + role + ".";

        // COMPLIANCE / AUDITS
        if ((path.startsWith("/compliance") || path.startsWith("/audits")) && !method.equals("GET"))
            return "Only COMPLIANCE_OFFICER can manage compliance records and audits. You are " + role + ".";

        // AUDIT LOGS
        if (path.startsWith("/audit-logs"))
            return "Only ADMIN can view the system audit trail. You are " + role + ".";

        return "You do not have permission to perform this action. You are " + role + ".";
    }
}
