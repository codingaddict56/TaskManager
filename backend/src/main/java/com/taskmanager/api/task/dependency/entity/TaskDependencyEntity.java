
package com.taskmanager.api.task.dependency.entity;

import com.taskmanager.api.task.entity.TaskEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "task_dependencies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_task_dependency",
                        columnNames = {"task_id", "depends_on_task_id"}
                )
        }
)
public class TaskDependencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "depends_on_task_id", nullable = false)
    private TaskEntity dependsOnTask;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Instant createdAt;

    protected TaskDependencyEntity() {
        // Required by JPA
    }

    public TaskDependencyEntity(
            TaskEntity task,
            TaskEntity dependsOnTask
    ) {
        this.task = task;
        this.dependsOnTask = dependsOnTask;
    }

    public UUID getId() {
        return id;
    }

    public TaskEntity getTask() {
        return task;
    }

    public TaskEntity getDependsOnTask() {
        return dependsOnTask;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
