
package com.taskmanager.api.project.service;

import com.taskmanager.api.project.dto.CreateProjectRequest;
import com.taskmanager.api.project.dto.ProjectResponse;
import com.taskmanager.api.project.dto.UpdateProjectRequest;
import com.taskmanager.api.project.entity.ProjectEntity;
import com.taskmanager.api.project.repository.ProjectRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        ProjectEntity project = new ProjectEntity(
                request.name(),
                request.description()
        );

        ProjectEntity savedProject = projectRepository.saveAndFlush(project);

        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getName(),
                savedProject.getDescription(),
                savedProject.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(project -> new ProjectResponse(
                        project.getId(),
                        project.getName(),
                        project.getDescription(),
                        project.getCreatedAt()
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found: " + id
                ));

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }


    @Transactional
    public ProjectResponse updateProject(
            UUID id,
            UpdateProjectRequest request) {

        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found: " + id
                ));

        project.setName(request.name());
        project.setDescription(request.description());

        ProjectEntity updatedProject = projectRepository.save(project);

        return new ProjectResponse(
                updatedProject.getId(),
                updatedProject.getName(),
                updatedProject.getDescription(),
                updatedProject.getCreatedAt()
        );
    }


    @Transactional
    public void deleteProject(UUID id) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found: " + id
                ));

        projectRepository.delete(project);
    }

}
