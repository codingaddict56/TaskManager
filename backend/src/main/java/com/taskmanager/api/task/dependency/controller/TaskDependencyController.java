
package com.taskmanager.api.task.dependency.controller;

import com.taskmanager.api.task.dependency.service.TaskDependencyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks/{taskId}/dependencies")
public class TaskDependencyController {

    private final TaskDependencyService dependencyService;

    public TaskDependencyController(
            TaskDependencyService dependencyService
    ) {
        this.dependencyService = dependencyService;
    }

    @PostMapping("/{dependsOnTaskId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDependency(
            @PathVariable UUID taskId,
            @PathVariable UUID dependsOnTaskId
    ) {
        dependencyService.addDependency(taskId, dependsOnTaskId);
    }

    @DeleteMapping("/{dependsOnTaskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDependency(
            @PathVariable UUID taskId,
            @PathVariable UUID dependsOnTaskId
    ) {
        dependencyService.removeDependency(taskId, dependsOnTaskId);
    }

    @GetMapping
    public List<UUID> getPrerequisiteTaskIds(
            @PathVariable UUID taskId
    ) {
        return dependencyService.getPrerequisiteTaskIds(taskId);
    }

    @GetMapping("/dependents")
    public List<UUID> getDependentTaskIds(
            @PathVariable UUID taskId
    ) {
        return dependencyService.getDependentTaskIds(taskId);
    }
}
