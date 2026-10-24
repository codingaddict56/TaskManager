package com.taskmanager.api.project.repository;

import com.taskmanager.api.project.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository
        extends JpaRepository<ProjectEntity, UUID> {
}
