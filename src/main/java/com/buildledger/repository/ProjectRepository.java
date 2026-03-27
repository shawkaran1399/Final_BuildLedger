package com.buildledger.repository;

import com.buildledger.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByManagerUserId(Long managerId);
    List<Project> findByStatus(String status);
}
