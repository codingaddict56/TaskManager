
CREATE TABLE task_dependencies (
                                   id UUID PRIMARY KEY,
                                   task_id UUID NOT NULL,
                                   depends_on_task_id UUID NOT NULL,
                                   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_task_dependencies_task
                                       FOREIGN KEY (task_id)
                                           REFERENCES tasks(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_task_dependencies_prerequisite
                                       FOREIGN KEY (depends_on_task_id)
                                           REFERENCES tasks(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT uq_task_dependency
                                       UNIQUE (task_id, depends_on_task_id),

                                   CONSTRAINT chk_task_dependency_not_self
                                       CHECK (task_id <> depends_on_task_id)
);

CREATE INDEX idx_task_dependencies_task_id
    ON task_dependencies(task_id);

CREATE INDEX idx_task_dependencies_depends_on_task_id
    ON task_dependencies(depends_on_task_id);
