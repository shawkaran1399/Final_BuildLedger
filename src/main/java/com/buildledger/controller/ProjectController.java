package com.buildledger.controller;

import com.buildledger.dto.request.ProjectRequest;
import com.buildledger.dto.response.ApiResponse;
import com.buildledger.dto.response.ProjectResponse;
import com.buildledger.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create project [ADMIN only]",
               description = "✅ ADMIN\n❌ PROJECT_MANAGER, VENDOR, CLIENT cannot create projects.")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", projectService.createProject(request)));
    }

    @GetMapping
    @Operation(summary = "Get all projects [ALL roles]")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved", projectService.getAllProjects()));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project by ID [ALL roles]")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.success("Project retrieved", projectService.getProjectById(projectId)));
    }

    @GetMapping("/manager/{managerId}")
    @Operation(summary = "Get projects by manager [ALL roles]")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved", projectService.getProjectsByManager(managerId)));
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update project [ADMIN only]",
               description = "✅ ADMIN\n❌ PROJECT_MANAGER, VENDOR, CLIENT cannot edit projects.")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Project updated", projectService.updateProject(projectId, request)));
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete project [ADMIN only]",
               description = "✅ ADMIN\n❌ No other role can delete projects.")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully"));
    }
}
