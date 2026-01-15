package com.teamtasks.dto.task;

import com.teamtasks.domain.task.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID organizationId,
        String title,
        String description,
        TaskStatus status,
        LocalDate dueDate,
        boolean overdue,
        UUID assignedToUserId,
        String assignedToEmail,
        UUID createdByUserId,
        Instant createdAt,
        Instant updatedAt
) {}