
package com.taskmanager.api.task.dto;

import com.taskmanager.api.task.TaskPriority;
import com.taskmanager.api.task.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        Integer estimatedMinutes,
        Instant createdAt
) {
}
