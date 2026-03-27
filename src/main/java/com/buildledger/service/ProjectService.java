package com.buildledger.service;

import com.buildledger.dto.request.ProjectRequest;
import com.buildledger.dto.response.ProjectResponse;
import com.buildledger.enums.ProjectStatus;
import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(ProjectRequest request);
    ProjectResponse getProjectById(Long projectId);
    List<ProjectResponse> getAllProjects();
    List<ProjectResponse> getProjectsByManager(Long managerId);
    ProjectResponse updateProject(Long projectId, ProjectRequest request);
    void deleteProject(Long projectId);
    ProjectResponse updateProjectStatus(Long projectId, ProjectStatus newStatus); // add this
}