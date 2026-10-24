
package com.taskmanager.api.focus.repository;

import com.taskmanager.api.task.TaskStatus;
import com.taskmanager.api.task.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FocusTaskRepository
        extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findByStatusNot(TaskStatus status);
}
