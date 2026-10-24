
package com.taskmanager.api.focus.dto;

import com.taskmanager.api.task.TaskPriority;

import java.time.LocalDate;
import java.util.UUID;

public record FocusRecommendation(
        UUID taskId,
        UUID projectId,
        String taskTitle,
        String projectName,
        TaskPriority priority,
        LocalDate dueDate,
        Integer estimatedMinutes,
        int score,
        String reason
) {
}
