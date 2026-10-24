
package com.taskmanager.api.focus.service;

import com.taskmanager.api.focus.dto.FocusRecommendation;
import com.taskmanager.api.focus.dto.FocusRequest;
import com.taskmanager.api.focus.repository.FocusTaskRepository;
import com.taskmanager.api.task.TaskPriority;
import com.taskmanager.api.task.TaskStatus;
import com.taskmanager.api.task.dependency.repository.TaskDependencyRepository;
import com.taskmanager.api.task.entity.TaskEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class FocusService {

    private final FocusTaskRepository taskRepository;
    private final TaskDependencyRepository dependencyRepository;

    public FocusService(
            FocusTaskRepository taskRepository,
            TaskDependencyRepository dependencyRepository
    ) {
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
    }

    @Transactional(readOnly = true)
    public List<FocusRecommendation> getRecommendations(FocusRequest request) {

        int availableMinutes = request.availableMinutes();
        LocalDate today = LocalDate.now();

        return taskRepository.findByStatusNot(TaskStatus.DONE)
                .stream()
                .filter(task -> fitsAvailableTime(task, availableMinutes))
                .filter(task -> !isBlocked(task))
                .map(task -> toRecommendation(task, today))
                .sorted(
                        Comparator.comparingInt(FocusRecommendation::score)
                                .reversed()
                )
                .limit(10)
                .toList();
    }

    private boolean fitsAvailableTime(
            TaskEntity task,
            int availableMinutes
    ) {
        Integer estimate = task.getEstimatedMinutes();

        // Tasks without estimates remain eligible.
        return estimate == null || estimate <= availableMinutes;
    }

    private boolean isBlocked(TaskEntity task) {
        return dependencyRepository.findByTaskId(task.getId())
                .stream()
                .map(dependency -> dependency.getDependsOnTask())
                .anyMatch(prerequisite -> prerequisite.getStatus() != TaskStatus.DONE);
    }

    private FocusRecommendation toRecommendation(
            TaskEntity task,
            LocalDate today
    ) {
        int score = calculateScore(task, today);

        return new FocusRecommendation(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getProject().getName(),
                task.getPriority(),
                task.getDueDate(),
                task.getEstimatedMinutes(),
                score,
                buildReason(task, today)
        );
    }

    private int calculateScore(TaskEntity task, LocalDate today) {
        int score = switch (task.getPriority()) {
            case URGENT -> 50;
            case HIGH -> 35;
            case MEDIUM -> 20;
            case LOW -> 10;
        };

        LocalDate dueDate = task.getDueDate();

        if (dueDate != null) {
            long daysUntilDue = ChronoUnit.DAYS.between(today, dueDate);

            if (daysUntilDue < 0) {
                score += 40; // Overdue
            } else if (daysUntilDue == 0) {
                score += 35; // Due today
            } else if (daysUntilDue <= 2) {
                score += 25; // Due within two days
            } else if (daysUntilDue <= 7) {
                score += 10; // Due within a week
            }
        }

        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            score += 5;
        }

        return score;
    }

    private String buildReason(TaskEntity task, LocalDate today) {
        StringBuilder reason = new StringBuilder();

        reason.append(switch (task.getPriority()) {
            case URGENT -> "Urgent priority";
            case HIGH -> "High priority";
            case MEDIUM -> "Medium priority";
            case LOW -> "Low priority";
        });

        LocalDate dueDate = task.getDueDate();

        if (dueDate != null) {
            long daysUntilDue = ChronoUnit.DAYS.between(today, dueDate);

            if (daysUntilDue < 0) {
                reason.append("; overdue by ")
                        .append(Math.abs(daysUntilDue))
                        .append(" day(s)");
            } else if (daysUntilDue == 0) {
                reason.append("; due today");
            } else if (daysUntilDue <= 2) {
                reason.append("; due soon");
            } else if (daysUntilDue <= 7) {
                reason.append("; due within a week");
            }
        }

        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            reason.append("; already in progress");
        }

        if (task.getEstimatedMinutes() != null) {
            reason.append("; estimated ")
                    .append(task.getEstimatedMinutes())
                    .append(" minutes");
        }

        return reason.toString();
    }
}
