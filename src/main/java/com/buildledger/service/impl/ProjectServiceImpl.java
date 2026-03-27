package com.buildledger.service.impl;

import com.buildledger.dto.request.ProjectRequest;
import com.buildledger.dto.response.ProjectResponse;
import com.buildledger.entity.Project;
import com.buildledger.entity.User;
import com.buildledger.exception.BusinessException;
import com.buildledger.exception.ResourceNotFoundException;
import com.buildledger.repository.ProjectRepository;
import com.buildledger.repository.UserRepository;
import com.buildledger.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        log.info("Creating project: {}", request.getName());

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date cannot be before start date");
        }

        Project.ProjectBuilder builder = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .budget(request.getBudget())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate());

        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getManagerId()));
            builder.manager(manager);
        }
        return mapToResponse(projectRepository.save(builder.build()));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long projectId) {
        return mapToResponse(findById(projectId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByManager(Long managerId) {
        return projectRepository.findByManagerUserId(managerId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Project project = findById(projectId);
        if (request.getName() != null) project.setName(request.getName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getLocation() != null) project.setLocation(request.getLocation());
        if (request.getBudget() != null) project.setBudget(request.getBudget());
        if (request.getStartDate() != null) project.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) project.setEndDate(request.getEndDate());
        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getManagerId()));
            project.setManager(manager);
        }
        return mapToResponse(projectRepository.save(project));
    }

    @Override
    public void deleteProject(Long projectId) {
        Project project = findById(projectId);
        projectRepository.delete(project);
    }

    private Project findById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
    }

    private ProjectResponse mapToResponse(Project p) {
        return ProjectResponse.builder()
                .projectId(p.getProjectId())
                .name(p.getName())
                .description(p.getDescription())
                .location(p.getLocation())
                .budget(p.getBudget())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .status(p.getStatus())
                .managerId(p.getManager() != null ? p.getManager().getUserId() : null)
                .managerName(p.getManager() != null ? p.getManager().getName() : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
