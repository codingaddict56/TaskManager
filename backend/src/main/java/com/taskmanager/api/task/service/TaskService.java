
package com.taskmanager.api.task.service;

import com.taskmanager.api.project.entity.ProjectEntity;
import com.taskmanager.api.project.repository.ProjectRepository;
import com.taskmanager.api.task.TaskPriority;
import com.taskmanager.api.task.dto.CreateTaskRequest;
import com.taskmanager.api.task.dto.TaskResponse;
import com.taskmanager.api.task.dto.UpdateTaskRequest;
import com.taskmanager.api.task.entity.TaskEntity;
import com.taskmanager.api.task.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final EntityManager entityManager;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            EntityManager entityManager
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        ProjectEntity project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        TaskPriority priority = request.priority() != null
                ? request.priority()
                : TaskPriority.MEDIUM;

        TaskEntity task = new TaskEntity(
                project,
                request.title(),
                request.description(),
                priority,
                request.dueDate(),
                request.estimatedMinutes()
        );

        TaskEntity savedTask = taskRepository.saveAndFlush(task);

        // Refresh database-generated fields such as created_at.
        entityManager.refresh(savedTask);

        return toResponse(savedTask);
    }

    @Transactional
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<TaskResponse> getTasksByProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Project not found"
            );
        }

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TaskResponse getTaskById(UUID id) {
        TaskEntity task = findTaskOrThrow(id);
        return toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        TaskEntity task = findTaskOrThrow(id);

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setEstimatedMinutes(request.estimatedMinutes());

        TaskEntity savedTask = taskRepository.save(task);
        return toResponse(savedTask);
    }

    @Transactional
    public void deleteTask(UUID id) {
        TaskEntity task = findTaskOrThrow(id);
        taskRepository.delete(task);
    }

    private TaskEntity findTaskOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"
                ));
    }

    private TaskResponse toResponse(TaskEntity task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getEstimatedMinutes(),
                task.getCreatedAt()
        );
    }
}
