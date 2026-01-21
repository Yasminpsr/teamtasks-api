CREATE TABLE task_time_entries (
  id UUID PRIMARY KEY,
  task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  started_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  started_at TIMESTAMP NOT NULL,
  ended_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_time_entries_task ON task_time_entries(task_id);
CREATE INDEX idx_time_entries_started_by ON task_time_entries(started_by);

-- Garante que exista no máximo 1 timer "rodando" por task (ended_at IS NULL)
CREATE UNIQUE INDEX uq_task_running_timer
ON task_time_entries(task_id)
WHERE ended_at IS NULL;