package com.teamtasks.dto.task;

import com.teamtasks.domain.task.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull TaskStatus status
) {}