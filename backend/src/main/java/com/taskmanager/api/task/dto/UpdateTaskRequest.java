
package com.taskmanager.api.task.dto;

import com.taskmanager.api.task.TaskPriority;
import com.taskmanager.api.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTaskRequest(

        @NotBlank(message = "Task title is required")
        @Size(max = 200, message = "Task title must not exceed 200 characters")
        String title,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotNull(message = "Task status is required")
        TaskStatus status,

        @NotNull(message = "Task priority is required")
        TaskPriority priority,

        LocalDate dueDate,

        @Positive(message = "Estimated minutes must be greater than zero")
        Integer estimatedMinutes

) {
}
