
package com.taskmanager.api.task.dependency.service;

import com.taskmanager.api.task.dependency.entity.TaskDependencyEntity;
import com.taskmanager.api.task.dependency.repository.TaskDependencyRepository;
import com.taskmanager.api.task.entity.TaskEntity;
import com.taskmanager.api.task.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class TaskDependencyService {

    private final TaskDependencyRepository dependencyRepository;
    private final TaskRepository taskRepository;

    public TaskDependencyService(
            TaskDependencyRepository dependencyRepository,
            TaskRepository taskRepository
    ) {
        this.dependencyRepository = dependencyRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void addDependency(UUID taskId, UUID dependsOnTaskId) {

        if (taskId.equals(dependsOnTaskId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A task cannot depend on itself"
            );
        }

        TaskEntity task = findTaskOrThrow(taskId);
        TaskEntity prerequisite = findTaskOrThrow(dependsOnTaskId);

        if (!task.getProject().getId().equals(
                prerequisite.getProject().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dependencies must belong to the same project"
            );
        }

        if (dependencyRepository.existsByTaskIdAndDependsOnTaskId(
                taskId, dependsOnTaskId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This dependency already exists"
            );
        }

        if (wouldCreateCycle(taskId, dependsOnTaskId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This dependency would create a circular dependency"
            );
        }

        TaskDependencyEntity dependency =
                new TaskDependencyEntity(task, prerequisite);

        dependencyRepository.save(dependency);
    }

    @Transactional
    public void removeDependency(UUID taskId, UUID dependsOnTaskId) {

        if (!dependencyRepository.existsByTaskIdAndDependsOnTaskId(
                taskId, dependsOnTaskId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dependency not found"
            );
        }

        dependencyRepository.deleteByTaskIdAndDependsOnTaskId(
                taskId, dependsOnTaskId
        );
    }

    @Transactional
    public List<UUID> getPrerequisiteTaskIds(UUID taskId) {

        findTaskOrThrow(taskId);

        return dependencyRepository.findByTaskId(taskId)
                .stream()
                .map(dependency -> dependency.getDependsOnTask().getId())
                .toList();
    }

    @Transactional
    public List<UUID> getDependentTaskIds(UUID taskId) {

        findTaskOrThrow(taskId);

        return dependencyRepository.findByDependsOnTaskId(taskId)
                .stream()
                .map(dependency -> dependency.getTask().getId())
                .toList();
    }

    private TaskEntity findTaskOrThrow(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found: " + taskId
                ));
    }

    private boolean wouldCreateCycle(
            UUID taskId,
            UUID dependsOnTaskId
    ) {
        // We are adding: taskId depends on dependsOnTaskId.
        // A cycle would occur if dependsOnTaskId already depends,
        // directly or indirectly, on taskId.

        Set<UUID> visited = new HashSet<>();
        Deque<UUID> pending = new ArrayDeque<>();
        pending.push(dependsOnTaskId);

        while (!pending.isEmpty()) {
            UUID currentId = pending.pop();

            if (currentId.equals(taskId)) {
                return true;
            }

            if (!visited.add(currentId)) {
                continue;
            }

            for (TaskDependencyEntity dependency :
                    dependencyRepository.findByTaskId(currentId)) {
                pending.push(dependency.getDependsOnTask().getId());
            }
        }

        return false;
    }
}
