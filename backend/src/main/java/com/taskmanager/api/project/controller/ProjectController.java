
package com.taskmanager.api.project.controller;

import com.taskmanager.api.project.dto.CreateProjectRequest;
import com.taskmanager.api.project.dto.ProjectResponse;
import com.taskmanager.api.project.dto.UpdateProjectRequest;
import com.taskmanager.api.project.service.ProjectService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // Create a new project
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(
            @Valid @RequestBody CreateProjectRequest request) {

        return projectService.createProject(request);
    }

    // Get all projects
    @GetMapping
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(@PathVariable UUID id) {
        return projectService.getProjectById(id);
    }


    @PutMapping("/{id}")
    public ProjectResponse updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request) {

        return projectService.updateProject(id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }

}


