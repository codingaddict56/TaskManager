
package com.taskmanager.api.task.dependency.repository;

import com.taskmanager.api.task.dependency.entity.TaskDependencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskDependencyRepository
        extends JpaRepository<TaskDependencyEntity, UUID> {

    List<TaskDependencyEntity> findByTaskId(UUID taskId);

    List<TaskDependencyEntity> findByDependsOnTaskId(UUID taskId);

    boolean existsByTaskIdAndDependsOnTaskId(
            UUID taskId,
            UUID dependsOnTaskId
    );

    void deleteByTaskIdAndDependsOnTaskId(
            UUID taskId,
            UUID dependsOnTaskId
    );
}
