
package com.taskmanager.api.task.controller;

import com.taskmanager.api.task.dto.CreateTaskRequest;
import com.taskmanager.api.task.dto.TaskResponse;
import com.taskmanager.api.task.dto.UpdateTaskRequest;
import com.taskmanager.api.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody CreateTaskRequest request
    ) {
        return taskService.createTask(request);
    }

    @GetMapping
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable UUID id) {
        return taskService.getTaskById(id);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getTasksByProject(
            @PathVariable UUID projectId
    ) {
        return taskService.getTasksByProject(projectId);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
    }
}
