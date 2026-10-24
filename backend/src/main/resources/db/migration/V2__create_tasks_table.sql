
CREATE TABLE tasks (
                       id UUID PRIMARY KEY,
                       project_id UUID NOT NULL,
                       title VARCHAR(200) NOT NULL,
                       description VARCHAR(2000),
                       status VARCHAR(20) NOT NULL DEFAULT 'TODO',
                       priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
                       due_date DATE,
                       estimated_minutes INTEGER,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_tasks_project
                           FOREIGN KEY (project_id)
                               REFERENCES projects(id)
                               ON DELETE RESTRICT,

                       CONSTRAINT chk_tasks_status
                           CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),

                       CONSTRAINT chk_tasks_priority
                           CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),

                       CONSTRAINT chk_tasks_estimated_minutes
                           CHECK (estimated_minutes IS NULL OR estimated_minutes > 0)
);

CREATE INDEX idx_tasks_project_id
    ON tasks(project_id);

CREATE INDEX idx_tasks_due_date
    ON tasks(due_date);
